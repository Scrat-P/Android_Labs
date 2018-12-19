package com.example.user.androidlabs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.androidlabs.database.OnProgressListener;
import com.example.user.androidlabs.rss.RssReader;

import java.net.MalformedURLException;

public class RssFragment extends Fragment implements RssReader.OnFeedItemLoadedListener,
        RssReader.OnItemsLoadedListener, OnProgressListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rss, container, false);
    }

    @Override
    public void onFeedItemLoadFailed(Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), R.string.rss_feed_loading_error_message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemsLoadFailed(Exception exception) {
        if (exception instanceof MalformedURLException) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    askToInputNewUrl(getString(R.string.incorrect_rss_url));
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), getString(R.string.loading_failed), Toast.LENGTH_LONG).show();
                    loadRssFeedFromCache();
                }
            });
        }
    }
}
