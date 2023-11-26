package com.example.umctest

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.umctest.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    lateinit var  song : Song
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? =null
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //xml 에 있는 것 메모리에 올리기
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }
        if(intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }
    }

    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second/60, song.second%60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw",this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        setPlayerStatus(song.isPlaying)

    }

    private  fun initSong() {
        if(intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                 intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second",0),
                intent.getIntExtra("playTime",0),
                intent.getBooleanExtra("isPlaying",false),
                intent.getStringExtra("music")!!)

        }
        startTimer()
    }
    fun setPlayerStatus(isPlaying : Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying
        if(isPlaying) {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }
    private fun startTimer() {
        timer = Timer(song.playTime,song.isPlaying)
        timer.start()
    }
    inner class Timer(private val playTime : Int, var isPlaying : Boolean = true): Thread() {
        private var second : Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            while(true) {
                if(second >= playTime) {
                    break;
                }

                if(isPlaying) {
                    sleep(50)
                    mills +=50

                    runOnUiThread {
                        binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                    }

                    if(mills % 1000 == 0f) {
                        runOnUiThread {
                            binding.songStartTimeTv.text  = String.format("%02d:%02d",second/60,second%60)
                        }
                        second ++
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songProgressSb.progress * song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 리소스 해제
        mediaPlayer = null
    }
}