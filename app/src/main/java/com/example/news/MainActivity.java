package com.example.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;
//https://newsapi.org/v2/top-headlines/sources?category=businessapiKey=API_KEY
//750b4baf67f0449dbb58e1e64b093798

public class MainActivity extends AppCompatActivity implements categoryRVAdapter.CategoryClickInterface {
    private RecyclerView newsRv,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles>articlesArrayList;
    private ArrayList<categoryRVModal> categoryRVModalArrayList;
    private categoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRv=findViewById(R.id.idRVnews);
        categoryRV=findViewById(R.id.idRVcategories);
        loadingPB=findViewById(R.id.idPBloading);
        articlesArrayList=new ArrayList<>();
        categoryRVModalArrayList=new ArrayList<>();
        newsRVAdapter=new NewsRVAdapter(articlesArrayList,this);
        categoryRVAdapter=new categoryRVAdapter(categoryRVModalArrayList,this,this::onCategoryClick);
        newsRv.setLayoutManager(new LinearLayoutManager(this));
        newsRv.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);
        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();
    }

    private void getCategories(){
        categoryRVModalArrayList.add(new categoryRVModal("General","https://elements-video-cover-images-0.imgix.net/files/222712617/Preview.jpg?auto=compress%2Cformat&fit=min&h=394&w=700&s=75fc1650e05752d545f9557e0dc8233b"));
        categoryRVModalArrayList.add(new categoryRVModal("Technology","https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fspecials-images.forbesimg.com%2Fimageserve%2F5ef3f7eec4f2390006f0c356%2FGUI--Graphical-User-Interface--concept-%2F960x0.jpg%3Ffit%3Dscale"));
        categoryRVModalArrayList.add(new categoryRVModal("Science","https://assets.weforum.org/community/image/3v8PB95CCSn86e5fowthRAybW4ajSY18z2FfVPi2spk.jpeg"));
        categoryRVModalArrayList.add(new categoryRVModal("Sports","https://images.unsplash.com/photo-1579952363873-27f3bade9f55?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=375&q=80"));
//        categoryRVModalArrayList.add(new categoryRVModal("General","https://images.unsplash.com/photo-1434030216411-0b793f4b4173?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1050&q=80"));
        categoryRVModalArrayList.add(new categoryRVModal("Business","https://www.wbcsd.org/var/site/storage/images/media/images/finance_img/25992-1-eng-GB/finance_img_i1140.jpg"));
        categoryRVModalArrayList.add(new categoryRVModal("Entertainment","https://www.e-spincorp.com/wp-content/uploads/2017/10/industry-media-entertainment.jpg"));
        categoryRVModalArrayList.add(new categoryRVModal("Health","https://idtxs3.imgix.net/si/40000/60/1B.jpg?w=1200&h=627&fit=crop&crop=faces,entropy&q=50"));
        categoryRVAdapter.notifyDataSetChanged();

    }
    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryURL="https://newsapi.org/v2/top-headlines?country=in&category="+category+"&apikey=750b4baf67f0449dbb58e1e64b093798";
        String url="https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=750b4baf67f0449dbb58e1e64b093798";
        String BASE_URL="https://newsapi.org/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI=retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        if(category.equals("All")){
            call=retrofitAPI.getAllNews(url);
        }
        else{
            call=retrofitAPI.getNewsByCategory(categoryURL);
        }
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                NewsModal newsModal= response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles>articles=newsModal.getArticles();
                for(int i=0;i<articles.size();i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(),articles.get(i).getUrlToImage(),articles.get(i).getUrl(),articles.get(i).getContent()));

                }
                newsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get News", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public void onCategoryClick(int position) {
        String category=categoryRVModalArrayList.get(position).getCategory();
        getNews(category);
    }
}