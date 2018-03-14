/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.stealth.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stealth.hushkbd.R;
import com.stealth.util.ImageViewSlice;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;

import static android.R.attr.button;

/**
 * A simple launcher activity containing a summary stealth description, stealth log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the stealth log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class Puzzle extends ListActivity {

    Button btn_refresh ; //ysk20161103

    public static final String TAG = "Puzzle";
    
    GridView gv_puzzle;

    boolean viewOnly = false ;
    boolean levelFinish = false;
    boolean tryState = false ; //ysk20161113
    int      tryPuzzleNum = -1 ; //ysk20161113
    boolean bSuccessTry = false ; //ysk20161113

    UserInfo blockUserInfo = null ;
    int blockId ;

    boolean webInit = false ;
    int gridHeight;
    int gridWidth;
    int puzzleNum = -1;
    int okPuzzleNum = -1;
    int dupPuzzleNum = -1;
    int piecesRow;
    int curPuzzles[] = new int[Const.MAX_PUZZLES];
    Bitmap bitmapPuzzle ;

    TextView tv_puzzle_text;
    TextView tv_block_help;
    FrameLayout fl_puzzle_part ;
    ImageViewSlice iv_puzzle_part;
    ImageView iv_puzzle;
    ImageView iv_youtube_top ;
    Animation flowAnim ;

    ListView lv ;

    String puzzleImageFullPath ;

    WebView wv_puzzle;
    boolean imagePressed = false;
    boolean pageFinished = false;

    //boolean Const.prevPuzzleState = false;
    ViewTreeObserver vto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);
        Const.puzzle = this ;

        //ysk20161114
        if (Const.getPuzzleNum() < 0)
            viewOnly = true ;

        tv_puzzle_text = (TextView) findViewById(R.id.tv_puzzle_text);
        tv_block_help = (TextView) findViewById(R.id.tv_block_help);
        iv_puzzle_part = (ImageViewSlice) findViewById(R.id.iv_puzzle_part);
        fl_puzzle_part = (FrameLayout)findViewById(R.id.fl_puzzle_part);

        iv_puzzle = (ImageView) findViewById(R.id.iv_puzzle);
        iv_youtube_top = (ImageView) findViewById(R.id.iv_youtube_top);
        wv_puzzle = (WebView) findViewById(R.id.wv_puzzle);
        gv_puzzle = (GridView) findViewById(R.id.gv_puzzle);

        btn_refresh = (Button)findViewById(R.id.btn_refresh) ; //ysk20161103

        flowAnim = AnimationUtils.loadAnimation(this, R.anim.flow) ;

        setTitle(strR(R.string.puzzle));

        PuzzleListAdapter cAdapter = new PuzzleListAdapter(this);
        setListAdapter(cAdapter);

        lv = (ListView)findViewById(android.R.id.list);

        lv.setOnItemClickListener(null);

        initVars() ;
        downloadPuzzle() ;
        reqLevels(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy()
    {
        Const.puzzle = null ;

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        wv_puzzle.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        wv_puzzle.onResume();
    }
    private void initVars()
    {
        if (Const.getPuzzleNum() < 0)
        {
            if (Const.prevPuzzleState) {
                for (int i = 0; i < Const.MAX_PUZZLES; i++)
                    curPuzzles[i] = 1;
                levelFinish = true ;
            }
            else
                copyPuzzles();
        }
        else {
            copyPuzzles();
            if (Const.remainedPuzzles() > 1) {
                tryState = true;
            }
        }

        Const.touchPuzzleNum = -1 ;
        tryPuzzleNum = -1 ; //ysk20161113
    }

    private void downloadPuzzle()
    {
        new DownloadPuzzleTask().execute() ;
    }

    private class DownloadPuzzleTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strData) {
            try {
                // . youtube url
                if (Const.getYoutubeUrl(Const.curLevel).equals(""))
                {
                    ReqParser.reqYoutubeUrl(Const.curLevel) ;
                }
                puzzleImageFullPath = Util.getDownloadFullPath(Const.puzzleImagePath, Const.curLevel+".png") ;

                if (!Util.existFile(puzzleImageFullPath))
                    bitmapPuzzle = Util.downloadUrlImage(Const.getYoutubeImageUrl(Const.curLevel));
                else
                    bitmapPuzzle = Util.getHushBitmap(puzzleImageFullPath);

            } catch (Exception e) {
                return 99;
            }
            return 0 ;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
            {
                if (!Util.existFile(puzzleImageFullPath))
                {
                    Util.saveBitmapToPngFile(bitmapPuzzle, Const.puzzleImagePath, Const.curLevel+".png") ;
                }

                preparePuzzleImage();

                recvPuzzleNum(Const.getPuzzleNum());

                if (tryState)
                {
                    if (okPuzzleNum >= 0) {
                            askTryMatch();
                    }
                    else {
                        tryState = false ;
                        dspDuplicated();
                        refresh() ;
                    }
                }
            }
        }
    }

    private void dspBlockHelp()
    {
    flowAnim.setAnimationListener(new FlowAnimationListner());
    tv_block_help.startAnimation(flowAnim);
    }

    private final class FlowAnimationListner implements Animation.AnimationListener{
        public void onAnimationEnd(Animation animation) {

        }
        public void onAnimationRepeat(Animation animation) {

        }
        public void onAnimationStart(Animation animation) {

        }
    }

    private void recvPuzzleNum(int num)
    {
        if (num < 0)
        {
            if (Const.prevPuzzleState)
            {
                initWeb();
            }
            refresh();
            return;
        }

        puzzleNum = num % (piecesRow * piecesRow) ;
        okPuzzleNum = -1 ;
        dupPuzzleNum = -1 ;

        int w = gridWidth / piecesRow ;
        int h = gridHeight / piecesRow ;

        iv_puzzle_part.setSlices(piecesRow, piecesRow);
        iv_puzzle_part.setTargetSlice(puzzleNum / piecesRow, puzzleNum % piecesRow);
        iv_puzzle_part.setImgResource(bitmapPuzzle);

        if (curPuzzles[puzzleNum] == 0) // just rcvd good number
        {
            okPuzzleNum = puzzleNum;
            curPuzzles[puzzleNum] = 1;
            Const.puzzles[puzzleNum] = 1;
            if (!tryState)
                checkLevelFinish() ; //ysk20161113
//                Const.savePuzzles(Const.curLevel);
        } else {
            dupPuzzleNum = puzzleNum;
        }

        if (levelFinish)
        {
            initWeb();
        }
        refresh() ;
    }

    private void dspPuzzleText()
    {
        //ysk20161127
        String str = "" ;

        if (tryState)
        {
            str = strR(R.string.puzzle_try_title) ;
            fl_puzzle_part.setVisibility(View.VISIBLE);
        }
        else {
            //ysk20171127
            int grade = Const.getGrade(Const.curLevel) ;
            str = strR(R.string.puzzle_grade) + (grade+1) + "(" +
                    Const.getCryptoSymbolFromLevel(Const.curLevel) + ")"; //ysk20161127
            if (puzzleNum < 0)
                fl_puzzle_part.setVisibility(View.INVISIBLE);
            else
                fl_puzzle_part.setVisibility(View.VISIBLE);

            str += " ";

            str += "HP=" + Const.getHP() + " ";

            if (puzzleNum >= 0) {
                if (okPuzzleNum == puzzleNum)
                    str += strR(R.string.puzzle_result_ok);
                else if (dupPuzzleNum == puzzleNum)
                    str += strR(R.string.puzzle_result_dup);
            }
        }

        tv_puzzle_text.setText(str);
    }

    private void preparePuzzleImage()
    {
        iv_puzzle.setImageBitmap(bitmapPuzzle);
        piecesRow = Const.getPiecesRow(Const.curLevel) ;

        gv_puzzle.setNumColumns(piecesRow);

        vto = gv_puzzle.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                gv_puzzle.getViewTreeObserver().removeOnPreDrawListener(this);
                gridHeight = gv_puzzle.getMeasuredHeight();
                gridWidth = gv_puzzle.getMeasuredWidth();
                gv_puzzle.setColumnWidth(gridWidth / piecesRow);
                gv_puzzle.invalidate();
                return true;
            }
        });
        gv_puzzle.setAdapter(new PuzzleAdapter(this));
    }

    public void refresh()
    {
        dspPuzzleText();
        tv_block_help.setText(strR(R.string.puzzle_help)); //ysk20161103
        gv_puzzle.invalidateViews();
        iv_puzzle_part.invalidate();
    }

    private void dspTitle() {
            setTitle(strR(R.string.puzzle_title));
    }

    private void initWeb()
    {
        if (webInit)
            return ;

         webInit = true ;

        wv_puzzle.getSettings().setJavaScriptEnabled(true);
        wv_puzzle.loadUrl(Const.getPuzzleUrl(Const.curLevel));
        wv_puzzle.measure(100, 100);
        wv_puzzle.getSettings().setUseWideViewPort(true);
        wv_puzzle.getSettings().setLoadWithOverviewMode(true);
        //remove flicker
        wv_puzzle.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            wv_puzzle.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            wv_puzzle.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wv_puzzle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                pageFinished = true ;
                if (imagePressed) {
                    iv_youtube_top.setVisibility(View.INVISIBLE);
                    iv_puzzle.setVisibility(View.INVISIBLE);
                    gv_puzzle.setVisibility(View.INVISIBLE);
                }
            }

                @SuppressLint("NewApi")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);

                // this will ignore the Ssl error and will go forward to your site
                handler.proceed();
                error.getCertificate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (levelFinish)
        {
            if (!imagePressed)
            {
                askExitAgain() ;
                return ;
            }
        }
        finish();
    }


    private void dspNoHP()
    {
        String str = getResources().getString(R.string.puzzle_remain_no) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    private void askUseHP()
    {
        String str = getResources().getString(R.string.puzzle_ask_hp) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        useHP() ;
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    private void askExitAgain()
    {
        String str = getResources().getString(R.string.puzzle_exit_ask) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish() ;
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    private void askPlayVideo()
    {
        String str = getResources().getString(R.string.puzzle_play_warning) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        prepareVideo();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    private void useHP()
    {
        new ReqUseHP().execute();
    }

    private class ReqUseHP extends AsyncTask<String, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(String... strData)
        {
            try
            {
                int n = 0;

                n = ReqParser.reqUseHP(Const.getMySid(), 1) ;
                return n ;
            }
            catch(Exception e)
            {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 0) // success
            {
                recvPuzzleNum(Const.touchPuzzleNum) ;
            }
            else if (result == 1)
            {
            }
        }
    }

    public void image_pressed(View v)
    {
        String num = (String)v.getTag() ;
        if ((num != null) && (Util.isPureNumeric(num))) {
            //ysk20161113
            if (tryState) {
                tryState = false ;
                tryPuzzleNum = Integer.parseInt(num);
                //ysk20161113
                if ((tryPuzzleNum > 0) && (tryPuzzleNum == okPuzzleNum))
                    bSuccessTry = true ;
                dspTryResult();
                return ;
            }
            else
                Const.touchPuzzleNum = Integer.parseInt(num);
        }


        refresh() ;

        if (levelFinish)
        {
            if (!Util.isWifiConnected())
            {
                askPlayVideo();
                return ;
            }

            prepareVideo();
        }
        else //if (!levelFinish)
        {
            int nHP = Const.getHP() ;
            if (Const.touchPuzzleNum >= 0 && curPuzzles[Const.touchPuzzleNum] == 0)
            {
                if (nHP == 0)
                {
                    dspNoHP() ;
                }
                else
                {
                    askUseHP() ;
                }
            }
            return;
        }
    }

    private void prepareVideo() {
        if (pageFinished) {
            gv_puzzle.setVisibility(View.INVISIBLE);
            iv_puzzle.setVisibility(View.INVISIBLE);
        } else {
            gv_puzzle.setVisibility(View.INVISIBLE);
        }
        imagePressed = true;
    }

    private void copyPuzzles()
    {
        for (int i=0; i < Const.MAX_PUZZLES; i++)
            curPuzzles[i] = Const.puzzles[i] ;
    }

    private void checkLevelFinish() //ysk20161028
    {
        levelFinish = true ;
        for (int i=0; i < piecesRow*piecesRow; i++)
        {
            if (Const.puzzles[i] != 1)
            {
                levelFinish = false ;
                break;
            }
        }

        if (levelFinish)
        {
            Const.upgradeMyOrgLevel(); // upgrade and savePuzzles
            dspLevelFinish() ;
        }
        else
        {
            Const.savePuzzles(Const.curLevel);
        }

        //ysk20161114
        Const.saveUsedPuzzleNum(Const.senderSid, Const.getPuzzleNum());

        reqUserCheck() ;
        return ; //ysk20161028
    }

    private void reqUserCheck()
    {
        //ysk20161113
        int plusHP = 0 ;
        if (bSuccessTry)
            plusHP = 1 ;

        if (Const.service1 != null)
            Const.service1.reqUserCheck(plusHP);
    }

    private void dspLevelFinish()
    {
        //ysk20161028
        int nString = R.string.puzzle_success ;

        if ((Const.getGrade(Const.curLevel) == Const.MAX_GRADE-1) && (Const.curLevel % 7 == 6))
            nString = R.string.puzzle_success_8 ;

        String str = getResources().getString(nString) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    public String strR(int id)
    {
        return(getResources().getString(id));
    }

    private void refreshList()
    {
        lv.invalidateViews();
    }

    //ysk20161103
    private void reqLevels(boolean bAll)
    {
        Const.bAllFriends = bAll ;
        new ReqLevels().execute() ;
    }

    private class ReqLevels extends AsyncTask<String, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(String... strData)
        {
            try
            {
                int n = 0;

                Const.userArrs = Const.dbDao.getUsers() ;
                String sFriends = Const.getFriendSids() ;

                //ysk20161103
                if (Const.isReqLevelOk() || Const.bAllFriends)
                    n = ReqParser.reqLevels(sFriends);

                //ysk20161027
                if (n == 0) {
                    if (!Const.bAllFriends)
                        sFriends = Const.getFriendSidsNoName();

                    if (!sFriends.equals(""))
                        n = ReqParser.reqNames(sFriends);
                }

                return n ;
            }
            catch(Exception e)
            {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 0) // success
            {
                //read again DB
                Const.userArrs = Const.dbDao.getUsers() ;
                refreshList() ;
            }
            else if (result == 1)
            {
            }
            //ysk20161103
            btn_refresh.setVisibility(View.VISIBLE);
        }
    }

    protected void selectFriend(int ix)
    {
    }

    protected void blockFriend(int ix)
    {
        UserInfo info = Const.userArrs.get(ix-1);
        blockUserInfo = info ;

        new ReqBlock().execute() ;
    }


    private class ReqBlock extends AsyncTask<String, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(String... strData)
        {
            try
            {
                int n = 0;

                blockId = blockUserInfo.BlockId ;
                if (blockId == 1) blockId = 0 ;
                else blockId = 1 ;

                n = ReqParser.reqBlock(Const.getMySid(), blockUserInfo.UserSid, blockId) ;
                return n ;
            }
            catch(Exception e)
            {
                return 99;
            }
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 0) // success
            {
                //read again DB
                dspBlockSuccess() ;
            }
        }
    }

    private void dspBlockSuccess()
    {
        String str = String.format((blockId==1)?strR(R.string.puzzle_block_success):strR(R.string.puzzle_allow_success),
                     blockUserInfo.UserName) ;

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public class PuzzleAdapter extends BaseAdapter {
        private Context mContext;

        public PuzzleAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            int n = piecesRow ;
            return n*n ;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                int size_col = gridWidth / piecesRow ;
                int size_row = gridHeight / piecesRow ;
                if (size_row > 0) {
                    imageView.setLayoutParams(new GridView.LayoutParams(size_col, size_row));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(0, 0, 0, 0);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String num = (String) v.getTag();
                            image_pressed(v);
                        }
                    });
                }
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(position+"");

            if (curPuzzles[position] == 0) // not received
            {
                int dnum = R.drawable.card_back ;
                if (position == Const.touchPuzzleNum)
                    dnum = R.drawable.card_back_touch ;
                imageView.setImageResource(dnum);
            }
            else // received puzzle
            {
                int dnum = R.drawable.card_frame ;

                //ysk20161113
                if (tryState)
                {
                    if (position == okPuzzleNum)
                        dnum = R.drawable.card_back ;
                }
                else
                {
                    if (position == okPuzzleNum)
                        dnum = R.drawable.card_frame_ok;
                    else if (position == dupPuzzleNum)
                        dnum = R.drawable.card_frame_dup;
                }

                imageView.setImageResource(dnum);
            }
            return imageView;
        }
    }

    /** Create a chain of targets that will receive log data */
    public class PuzzleListAdapter extends BaseAdapter {

        private Context mContext ;

        public PuzzleListAdapter(Context c) {
            mContext = c;
        }


        @Override
        public int getCount()
        {
            if (Const.userArrs == null)
                return 1 ;
            return Const.userArrs.size()+1 ;
        }

        @Override
        public Object getItem(int position)
        {
            if (position == 0)
                return null ;
            else
            {
                return Const.userArrs.get(position-1) ;
            }
        }

        @Override
        public long getItemId(int position)
        {
            return (position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater vi = LayoutInflater.from(this.mContext);
                convertView = vi.inflate(R.layout.puzzle_item, null);

                viewHolder = new ViewHolder();

                viewHolder.position = position;
                viewHolder.ll_friends = (LinearLayout) convertView.findViewById(R.id.ll_friends);
                viewHolder.ll_friends.setTag(viewHolder);
                viewHolder.ll_friends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackgroundColor(Color.MAGENTA);
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        int ix = viewHolder.position;

                        selectFriend(ix);
                    }
                });


                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_name.setTag(viewHolder);
                viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        int ix = viewHolder.position;
                        selectFriend(ix);
                    }
                });

                convertView.setTag(viewHolder);

                viewHolder.tv_parts = (TextView) convertView.findViewById(R.id.tv_parts);
                viewHolder.tv_parts.setTag(viewHolder);
                viewHolder.tv_parts.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        int ix = viewHolder.position;
                        selectFriend(ix);
                    }
                });
            } else {
                //Get view holder back
                viewHolder = (ViewHolder) convertView.getTag();
                Const.MyLog("GrouplList", "convertView != null");
            }

            viewHolder.position = position;
            viewHolder.ll_friends = (LinearLayout) convertView.findViewById(R.id.ll_friends);
            viewHolder.ll_friends.setBackgroundColor(Color.TRANSPARENT);

            //name
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            //level
            viewHolder.tv_level = (TextView) convertView.findViewById(R.id.tv_level);

            //parts
            viewHolder.tv_parts = (TextView) convertView.findViewById(R.id.tv_parts);

            //check box
            //viewHolder.cb_block = (CheckBox) convertView.findViewById(R.id.cb_block);
            viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ViewHolder viewHolder = (ViewHolder) v.getTag();
                    int ix = viewHolder.position;
                    blockFriend(ix);
                }
            });

            if (position == 0) {
                //title
                viewHolder.tv_name.setText(strR(R.string.puzzle_item_name));
                viewHolder.tv_level.setText(strR(R.string.puzzle_item_grade)); //ysk20161127
                viewHolder.tv_parts.setText(strR(R.string.puzzle_item_parts));
                //viewHolder.cb_block.setVisibility(View.INVISIBLE);
            } else {
                UserInfo info = (UserInfo) getItem(position); //Const.userArrs.get(position);
                viewHolder.tv_name.setText(info.UserName);

                //ysk20161127
                int grade = Const.getGrade(info.UserLevel);

                if (grade >= Const.MAX_GRADE) {
                    viewHolder.tv_level.setText ((grade+1) + "(" + Const.CryptoSymbolsLite[Const.MAX_GRADE] + ")");
                } else {
                    viewHolder.tv_level.setText ((grade+1) + "(" + Const.CryptoSymbolsLite[grade] + ")");
                }
                viewHolder.tv_parts.setText(info.PartsCnt + "");
                //ysk20161103
                /*
                viewHolder.cb_block.setVisibility(View.VISIBLE);
                viewHolder.cb_block.setText(strR(R.string.puzzle_block));
                if (info.BlockId == 1)
                    viewHolder.cb_block.setChecked(true);
                else
                    viewHolder.cb_block.setChecked(false);
                    */
            }
            return convertView;
        }
    }

    public static class ViewHolder
    {
        int position ;
        LinearLayout ll_friends ;
        TextView tv_name;
        TextView tv_level;
        TextView tv_parts;
        //CheckBox cb_block ;
    }

    //ysk20161103
    public void refresh_pressed(View v)
    {
        btn_refresh.setVisibility(View.INVISIBLE);
        reqLevels(true);
    }

    //ysk20161107
    public void puzzle_reg_pressed(View v)
    {
        gotoPuzzleReg() ;
    }

    public void gotoPuzzleReg()
    {
        Intent intent = new Intent(this, PuzzleReg.class);

        startActivity(intent);
    }

    //ysk20161113
    private void askTryMatch()
    {
        if (!Const.isAskMatchYes())
            return ;

        String str = getResources().getString(R.string.puzzle_try_ask) ;
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setNegativeButton(R.string.dsp_no_more, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.saveAskMatchYes(false) ;
                    }
                })
                .show();
    }


    private void dspTryResult()
    {
        String str ;
        if (bSuccessTry)
            str = getResources().getString(R.string.puzzle_try_success) ;
        else
            str = getResources().getString(R.string.puzzle_try_fail) ;

        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        checkLevelFinish() ;
                        refresh() ;
                    }
                })
                .show();
    }

    private void dspDuplicated()
    {
        if (!Const.isDspDup())
            return ;

        String str ;
            str = getResources().getString(R.string.puzzle_try_dup) ;

        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setNegativeButton(R.string.dsp_no_more, new DialogInterface.OnClickListener()
                {	// ?
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Const.saveDspDup(false) ;
                    }
                })
                .show();
    }
}
