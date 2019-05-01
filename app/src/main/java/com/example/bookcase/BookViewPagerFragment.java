package com.example.bookcase;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookViewPagerFragment extends Fragment {

    ArrayList<Book> booksArrList;
    ViewPager vp;
    PagerAdapter pagerAdapter;
    BookDetailsFragment newFragment;
    Book book;


    public BookViewPagerFragment() {
        // Required empty public constructor
    }

    public static BookViewPagerFragment newInstance(String param1, String param2) {
        BookViewPagerFragment fragment = new BookViewPagerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);
        pagerAdapter = new PagerAdapter(getFragmentManager());

        booksArrList = new ArrayList<>();

        vp = v.findViewById(R.id.viewPager);
        vp.setAdapter(pagerAdapter);

        return v;
    }

    public void addPager(final ArrayList bookArr){
        booksArrList.clear();
        booksArrList.addAll(bookArr);

        int i = 0;
        while (i< booksArrList.size()) {
            book = booksArrList.get(i);
            newFragment = BookDetailsFragment.newInstance(book);
            pagerAdapter.add(newFragment);
            pagerAdapter.notifyDataSetChanged();

            i++;
        }

        pagerAdapter.getItemPosition(book);

    }

    class PagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<BookDetailsFragment> pagerFragments;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            pagerFragments = new ArrayList<>();
        }

        public void add(BookDetailsFragment fragment){
            pagerFragments.add(fragment);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int i) {
            return pagerFragments.get(i);
        }

        @Override
        public int getCount() {
            return pagerFragments.size();
        }
    }

}
