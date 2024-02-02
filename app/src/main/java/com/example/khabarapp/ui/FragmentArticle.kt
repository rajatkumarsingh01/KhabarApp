package com.example.khabarapp.ui
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.khabarapp.R
import com.example.khabarapp.Utils
import com.example.khabarapp.db.SavedArticle
import com.example.khabarapp.db.Source
import com.example.khabarapp.mvvm.NewsDatabase
import com.example.khabarapp.mvvm.NewsRepo
import com.example.khabarapp.mvvm.NewsViewModel
import com.example.khabarapp.mvvm.NewsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentArticle : Fragment() {
    lateinit var viewModel:NewsViewModel
    lateinit var args: FragmentArticleArgs
     var stringCheck=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setTitle("Article View ")

        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        args=FragmentArticleArgs.fromBundle(requireArguments())

        //initialize the views of Article frag

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        val textTitle: TextView = view.findViewById(R.id.tvTitle)
        val tSource: TextView = view.findViewById(R.id.tvSource)
        val tDescription: TextView = view.findViewById(R.id.tvDescription)
        val tPubslishedAt: TextView = view.findViewById(R.id.tvPublishedAt)
        val imageView: ImageView = view.findViewById(R.id.articleImage)

        val source = Source(args.article.source!!.id, args.article.source!!.name)

        textTitle.setText(args.article.title)
        tSource.setText(source.name)
        tDescription.setText(args.article.description)
        tPubslishedAt.setText(Utils.DateFormat(args.article.publishedAt))

        Glide.with(requireActivity())
            .load(args.article.urlToImage)
            .into(imageView)
//all the news are saved in the list
        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer { savedArticles ->
            for (savedArticle in savedArticles) {
                if (args.article?.title == savedArticle.title) { // Adding safe call operator
                    stringCheck = savedArticle.title
                    break
                }
            }
        })

        fab.setOnClickListener {
            val article = args.article // Extract article to a local variable
            if (article != null) { // Check if article is not null
                if (article.title == stringCheck) {
                    Log.e("fragArg", "exists")
                    Toast.makeText(context, "Article exists in Saved List", Toast.LENGTH_SHORT).show()
                } else {
                    val source = Source(article.source?.id ?: "", article.source?.name ?: "") // Null safety for source properties
                    viewModel.insertArticle(
                        SavedArticle(
                            0,
                            article.description ?: "",
                            article.publishedAt ?: "",
                            source,
                            article.title ?: "",
                            article.url ?: "",
                            article.urlToImage ?: ""
                        )
                    )
                    Log.e("fragArg", "saved")
                    Toast.makeText(context, "Saved SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                    view.findNavController().navigate(R.id.action_fragmentArticle_to_fragmentSavedNews)
                }
            } else {
                Log.e("fragArg", "Article is null")
                // Handle null article case here if needed
            }
        }

    }

    }

