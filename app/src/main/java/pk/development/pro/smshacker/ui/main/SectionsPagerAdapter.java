package pk.development.pro.smshacker.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pk.development.pro.smshacker.R;
import pk.development.pro.smshacker.frags.HackerFrag;
import pk.development.pro.smshacker.frags.ExpertFrag;
import pk.development.pro.smshacker.frags.AtCommander;
import pk.development.pro.smshacker.frags.HelpFrag;
import pk.development.pro.smshacker.frags.NoobFrag;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = {
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_expert2,
            R.string.tab_text_3,
            R.string.tab_help
    };

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1: return new HackerFrag();
            case 2: return new ExpertFrag();//hacker
            case 3: return new AtCommander();//at frag
            case 4: return new HelpFrag();
        }

        return new NoobFrag();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }
}