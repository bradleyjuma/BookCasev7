package com.example.bookcase;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookDetailsFragment extends Fragment {

    public BookDetailsFragment() {
        // Required empty public constructor
    }


    Context c;
    String title, author, publish;
    TextView tv, progressText;
    ImageView imageView;
    Book pagerBooks;
    ImageButton playBTN, pauseBTN, stopBTN;
    SeekBar seekBar, dl_progress;
    ProgressBar progressBar;
    Button dl_BTN;
    public static final String BOOK_KEY = "myBook";
    private BookDetailsInterface mListener;

    public static BookDetailsFragment newInstance(Book bookList) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BOOK_KEY, bookList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            pagerBooks = getArguments().getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        tv = view.findViewById(R.id.bookTitle);
        imageView = view.findViewById(R.id.bookImage);

        playBTN = view.findViewById(R.id.playButton);
        pauseBTN = view.findViewById(R.id.pauseButton);
        stopBTN = view.findViewById(R.id.stopButton);
        seekBar = view.findViewById(R.id.seekBar);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        dl_BTN = view.findViewById(R.id.download_btn);
        dl_progress = view.findViewById(R.id.dl_progress);


        if(getArguments() != null) {
            displayBook(pagerBooks);
        }



        dl_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                // check the button value
                if(dl_BTN.getText().toString().equals("Download")) {
                    String query = "https://kamorris.com/lab/audlib/download.php?id=" + Integer.toString(pagerBooks.getId());
                    new downloadBook(dl_progress, dl_BTN, c).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                    //Updated button to communicate with the book the user's requesting to Download
                    dl_BTN.setText(R.string.delete_txt);
                } else { // It's a delete button
                    //Updated button to communicate with the book the user's requesting to Delete
                    dl_BTN.setText(R.string.download_txt);
                }
            }
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void displayBook(final Book bookObj){
        //Gets the authors name
        author = bookObj.getAuthor();
        //Gets the titles name
        title = bookObj.getTitle(); publish = bookObj.getPublished();
        //Sets the given variables above.
        tv.setText(" \"" + title + "\" "); tv.append(" " + author); tv.append(" " + publish);
        tv.setTextSize(30); //Text resize
        tv.setTextColor(Color.BLACK); //Text color change.
        String imageURL = bookObj.getCoverURL();
        //Loads Picasso image info
        Picasso.get().load(imageURL).into(imageView);
        //PLay button listener
        playBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BookDetailsInterface)c).playBook(bookObj.getId());
            }
        });
        //Pause button listener
        pauseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BookDetailsInterface)c).pauseBook();
            }
        });
        //Stop button listener
        stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BookDetailsInterface)c).stopBook();
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBar.setProgress(progress);
                ((BookDetailsInterface)c).seekBook(progress);
                progressText.setText(" " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //not needed
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookListFragment.BookInterface) {
            mListener = (BookDetailsInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        this.c = context;
    }

    public interface BookDetailsInterface{
        void playBook(int id);
        void pauseBook();
        void stopBook();
        void seekBook(int position);
    }
    @SuppressLint("StaticFieldLeak")
    private static class downloadBook extends AsyncTask<String, Integer, String> {

        SeekBar dl_progress;
        Button dl_BTN;
        Context ctx;
        char id;

        downloadBook(SeekBar dl_progress, Button dl_BTN, Context ctx) {
            this.dl_progress = dl_progress;
            this.dl_BTN = dl_BTN;
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            //Make's the download progress bar visible.
            dl_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... surl) {
            id = surl[0].charAt(surl[0].length() - 1);
            InputStream in = null;
            OutputStream out = null;
            HttpURLConnection connection;
            connection = null;
            try {
                URL url = new URL(surl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                in = connection.getInputStream();
                out = new FileOutputStream(ctx.getFilesDir().getPath() + "/" + id + ".mp3");

                byte data[] = new byte[4096];
                AtomicLong total = new AtomicLong();
                int count;
                while((count = in.read(data)) != -1) {
                    if(isCancelled()) {
                        in.close();
                        return null;
                    }
                    total.addAndGet(count);
                    if(fileLength > 0)
                        publishProgress((int) (total.get() * 100 / fileLength));
                    out.write(data, 0, count);
                }
            } catch(Exception e) {
                return e.toString();
            } finally {
                try {
                    if(out!=null)
                        out.close();
                    if (in != null) {
                        in.close();
                    }
                } catch(IOException ignored) {
                }

                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            dl_progress.setIndeterminate(false);
            dl_progress.setMax(100);
            dl_progress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //hide the download bar since download is finished
            dl_progress.setVisibility(View.INVISIBLE);

            //check if file downloaded
            if(result != null) {
                Toast.makeText(ctx, "Download error: " + result, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(ctx, "File downloaded", Toast.LENGTH_SHORT).show();
                dl_BTN.setText(R.string.delete_txt);
            }
        }
    }






}
