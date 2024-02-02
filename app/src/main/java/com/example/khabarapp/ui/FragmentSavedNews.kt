@file:Suppress("DEPRECATION")

package com.example.khabarapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khabarapp.R
import com.example.khabarapp.adapters.ArticleAdapter
import com.example.khabarapp.adapters.SavedArticlesAdapter
import com.example.khabarapp.mvvm.NewsDatabase
import com.example.khabarapp.mvvm.NewsRepo
import com.example.khabarapp.mvvm.NewsViewModel
import com.example.khabarapp.mvvm.NewsViewModelFactory


class FragmentSavedNews : Fragment(), MenuProvider {

    lateinit var viewModel:NewsViewModel
    lateinit var newsAdapter:SavedArticlesAdapter
    lateinit var rv:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)

        setHasOptionsMenu(true)


        (activity as AppCompatActivity).supportActionBar?.setTitle("Saved News")


        rv=view.findViewById(R.id.rvSavedNews)


        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
        newsAdapter =SavedArticlesAdapter()

        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer {

            newsAdapter.setlist(it)
            setUpRecyclerView()

        })
    }

    private fun setUpRecyclerView() {


        rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.menu, menu)
        val searchIcon=menu.findItem(R.id.searchNews)
        val savedIcon=menu.findItem(R.id.savedNewsFrag)

        searchIcon.setVisible(false)
        savedIcon.setVisible(false)


        super.onCreateOptionsMenu(menu,menuInflater)



    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId==R.id.deleteAll){
            val builder=AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Menu")
            builder.setMessage("Are You sure to Delete?")
            builder.setPositiveButton("Delete All"){dailog,which->
               viewModel.deleteAll()
               Toast.makeText(context,"Successfully Deleted",Toast.LENGTH_SHORT).show()

                view?.findNavController()?.navigate(R.id.action_fragmentSavedNews_to_fragmentBreakingNews)

            }
            builder.setNegativeButton("Cancel"){dailog,which->
                dailog.dismiss()


            }

            val dailog=builder.create()
            dailog.show()
        }
        return true

    }


}