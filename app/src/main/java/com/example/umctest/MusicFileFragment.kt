package com.example.umctest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umctest.databinding.FragmentLockerMusicfileBinding
import com.example.umctest.databinding.FragmentLockerSavedsongBinding

class MusicFileFragment: Fragment() {

    lateinit var binding: FragmentLockerMusicfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLockerMusicfileBinding.inflate(inflater, container, false)

        return binding.root
    }
}