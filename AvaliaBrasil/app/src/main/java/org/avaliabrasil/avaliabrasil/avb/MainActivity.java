package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.avaliabrasil.avaliabrasil.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: Implementar variável userId ou outra corretamente!
    private static String USRID = "userId";
    private int userId = 0;
    private static String GOOGLEPLACESID = "googlePlacesId";


    // Definindo o Google Place Id que será passado para o Place Activity
    public String googlePlacesId = "asdasgd8218hdddDdsSAD";

    // Variáveis para o ViewPager e Tabs!
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Instruções para criar o Section Page!!
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_page_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        // Quando adicionei uma activity chamada dentro do fragment, io app parava quando clicava-se no botão voltar do Place Activity!
        // Comentei estas linhas e funcinou, mas não entendi porque.
        //String userId = getIntent().getExtras().getString(USRID);
        // Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        //TODO: Tirar isto e colocar código real depois!
        // Defining the UserId:
        userId = getIntent().getExtras().getInt(USRID);
        Toast.makeText(this,"Id de Usuário : " + userId,Toast.LENGTH_SHORT).show();

    }

    // Métodos para o NavDrawer

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TODO: Tirar este menu!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

     return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Métodos para a TabView!

    // PlacesMapFragment: Map of nearby found places
    public static class PlacesMapFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        //TODO: Colocar outros parâmetros se necessário
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlacesMapFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlacesMapFragment newInstance(int sectionNumber) {
            PlacesMapFragment fragment = new PlacesMapFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_places_map, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.places_map_text_view);

            // Não vou alterar o texto do Places Map!
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    //PlacesListFragment: List of nearby found places
    public static class PlacesListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlacesListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlacesListFragment newInstance(int sectionNumber) {
            PlacesListFragment fragment = new PlacesListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.places_list_text_view);

            // Não vou alterar o texto do Places List!
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            //TODO: Criar um case aqui.
            //Se position é 0, retornar nova instância do PlacesListFragment
            //Se position é 1, retornar nova instância do PlacesMapFragment

            switch (position) {
                case 0:
                    return PlacesListFragment.newInstance(position + 1);
                case 1:
                    return PlacesMapFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.places_list);
                case 1:
                    return getString(R.string.places_map);
            }
            return null;
        }
    }

    // Button to open a activity

    // TODO: Tempo problema aqui...
    public void startPlaceActivity (View view) {
        Intent intent_place_activity = new Intent(MainActivity.this, PlaceActivity.class);
        intent_place_activity.putExtra(USRID, userId);
        intent_place_activity.putExtra(GOOGLEPLACESID, googlePlacesId);
        startActivity(intent_place_activity);
    }


}
