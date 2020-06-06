package com.fkoteam.anairdrum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private VideoView simpleVideoView1,simpleVideoView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        Preferencias.init(getApplicationContext());
        if (!Preferencias.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.viewpager1,
                R.layout.viewpager2,
                R.layout.viewpager3,
                R.layout.viewpager4};
        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }
    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
    private void launchHomeScreen() {
        Preferencias.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
    ViewPager.OnCreateContextMenuListener createContextMenuListener=new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    };
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            stopAll();
            if(position==0) {
                simpleVideoView1 = (VideoView) findViewById(R.id.video1);
                simpleVideoView1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video1));
                simpleVideoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        simpleVideoView1.start();
                    }
                });
            }

            if(position==1) {
                simpleVideoView2 = (VideoView) findViewById(R.id.video2);
                simpleVideoView2.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video2));
                simpleVideoView2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        simpleVideoView2.start();
                    }
                });
            }

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int  positionOffsetPixels)
        {

        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void stopAll() {
        if( simpleVideoView1!=null)
            simpleVideoView1.stopPlayback();
        if( simpleVideoView2!=null)
            simpleVideoView2.stopPlayback();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        public MyViewPagerAdapter() {
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            if(position==0) {
                simpleVideoView1 = (VideoView) findViewById(R.id.video1);
                simpleVideoView1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video1));
                simpleVideoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        simpleVideoView1.start();
                    }
                });
            }
            else
            {
                if( simpleVideoView1!=null)
                    simpleVideoView1.stopPlayback();
            }

            return view;
        }
        @Override
        public int getCount() {
            return layouts.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAll();
    }
}
