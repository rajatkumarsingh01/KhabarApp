package com.example.khabarapp.ui
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.khabarapp.R
import com.example.khabarapp.Utils
import com.example.khabarapp.db.Article
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
    private lateinit var fabShare: FloatingActionButton
    private lateinit var fab:FloatingActionButton
    private lateinit var fabSave: FloatingActionButton
    private var isFabMenuOpen = false




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
         fab =view.findViewById<FloatingActionButton>(R.id.fab)
         fabSave = view.findViewById<FloatingActionButton>(R.id.fabSave)
         fabShare = view.findViewById<FloatingActionButton>(R.id.fabShare)

        // Set initial visibility of sub FABs to GONE
        fabSave.visibility = View.GONE
        fabShare.visibility = View.GONE

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
        // for share=ing news article

        fab.setOnClickListener {
            if (isFabMenuOpen) {
                closeFabMenu()
            } else {
                openFabMenu()
            }

        }

        fabShare.setOnClickListener {
            shareArticle(args.article)
            closeFabMenu()
        }
//all the news are saved in the list
        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer { savedArticles ->
            for (savedArticle in savedArticles) {
                if (args.article?.title == savedArticle.title) { // Adding safe call operator
                    stringCheck = savedArticle.title
                    break
                }
            }
        })

        fabSave.setOnClickListener {
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
            closeFabMenu()
        }

    }

    private fun openFabMenu() {
        fabShare.visibility = View.VISIBLE
        fabSave.visibility = View.VISIBLE
        fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.fab_icon_close))

        isFabMenuOpen = true
    }

    private fun closeFabMenu() {
        fabShare.visibility = View.GONE
        fabSave.visibility = View.GONE
        fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.fab_icon_more))
        isFabMenuOpen = false
    }

    private fun shareArticle(article: Article) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, article.url)

        val shareChooser = Intent.createChooser(shareIntent, "Share via")

        val packageNames = listOf(
            "com.whatsapp",
            "com.facebook.orca", // Messenger
            "org.telegram.messenger",
            "com.android.mms", // Messages (SMS)
            "com.google.android.gm" // Gmail

        )
        val sendIntents = mutableListOf<Intent>()
        for (packageName in packageNames) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, article.title)
            intent.putExtra(Intent.EXTRA_TEXT, article.url)
            intent.setPackage(packageName)
            if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
                sendIntents.add(intent)
            }
        }

        if (sendIntents.isNotEmpty()) {
            shareChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, sendIntents.toTypedArray())
            startActivity(shareChooser)
        } else {
            Toast.makeText(context, "No app found to share", Toast.LENGTH_SHORT).show()
        }

    }
    }

