package pk.development.pro.smshacker.frags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import pk.development.pro.smshacker.Constants;
import pk.development.pro.smshacker.R;

public class HelpFrag extends Fragment {
    private WebView webview;


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);

        webview = (WebView) root.findViewById(R.id.webview);
        webview.loadUrl("file:///android_asset/help.html");


        return root;
    }
}
