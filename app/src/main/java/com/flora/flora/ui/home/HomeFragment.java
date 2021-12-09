package com.flora.flora.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.flora.flora.R;
import com.flora.flora.databinding.FragmentHomeBinding;
import com.flora.flora.ui.FavoriteFragment;
import com.flora.flora.ui.FlowerFragment;
import com.flora.flora.ui.GiftFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    //Intialize elements which are used in that layout
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;


    //Initialize fragment
    GiftFragment giftFragment;
    FlowerFragment flowerFragment;
    FavoriteFragment favoriteFragment;

    //create previous menu item
    MenuItem prevMenuItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //find elements by ids
        viewPager = root.findViewById(R.id.viewpager);
        bottomNavigationView = root.findViewById(R.id.navigation);

        //add action on bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottom_nav_gift:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.bottom_nav_flower:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.bottom_nav_favorite:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        //add action on view pager chnage
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        giftFragment=new GiftFragment();
        flowerFragment = new FlowerFragment();
        favoriteFragment = new FavoriteFragment();

        adapter.addFragment(giftFragment);
        adapter.addFragment(flowerFragment);
        adapter.addFragment(favoriteFragment);
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

}