import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pianosense.ComparisonResult
import com.example.pianosense.Music
import com.example.pianosense.NoteInfo
import com.example.pianosense.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MusicViewModel : ViewModel() {

    private val _analysisResults = MutableLiveData<List<ComparisonResult>>()
    val analysisResults: LiveData<List<ComparisonResult>> get() = _analysisResults

    fun setAnalysisResults(noteInfoList: List<NoteInfo>) {
        val comparisonResults = noteInfoList.map { noteInfo ->
            ComparisonResult(
                originalNote = noteInfo.originalNote?.let { NoteInfo(it, null, noteInfo.timestamp, false) },
                recordedNote = noteInfo.recordedNote?.let { NoteInfo(null, it, noteInfo.timestamp, false) },
                isCorrect = noteInfo.originalNote == noteInfo.recordedNote // Örneğin: notalar eşleşiyor mu
            )
        }
        _analysisResults.value = comparisonResults
    }



    // Müzik listesi için LiveData
    val musicList = MutableLiveData<List<Music>>()

    init {
        // Varsayılan müzik listesini tanımladım
        fetchDynamicMusic()
        musicList.value = listOf(
            Music(1, "Kayıt 1", "Beethoven", R.drawable.bethoven, "originalMusic1.wav", bpm=60),
            Music(2, "Kayıt 2 ", "Mozart", R.drawable.mozart, "originalMusic2.wav",bpm=100),
            Music(3, "9. Senfoni", "Bethoven", R.drawable.bethoven, "originalMusic1.wav",bpm=120),
            Music(4, "Divenire", "Ludovico Einaudi", R.drawable.ludovico_einaudi, "originalMusic1.wav",bpm=60),
            Music(5, "Hit the Road Jack", "Ray Charles", R.drawable.ray_charles, "originalMusic1.wav",bpm=60),
            Music(6, "Hold the Line", "Toto", R.drawable.mozart, "originalMusic1.wav",bpm=60),
            Music(7, "Someone Like You", "Adele", R.drawable.adele, "originalMusic1.wav",bpm=60),
            Music(8, "Comptine d’un autre été l’après", "Yann Tiersen", R.drawable.yann_tiersen, "originalMusic1.wav",bpm=60),
            Music(9, "Parisienne Moonlight", "Anathema", R.drawable.anathema, "originalMusic1.wav",bpm=60),
            Music(10, "İmagine", "John Lennon", R.drawable.john_lennon, "originalMusic1.wav",bpm=60),
            Music(11, "Brother John", "Anonim", R.drawable.brother_john, "originalMusic1.wav",bpm=60),
            Music(12, "Für Elise", "Beethoven", R.drawable.bethoven, "originalMusic1.wav",bpm=60),
            Music(13, "Valse", "Evegny Grinko", R.drawable.evegny_grinko, "originalMusic1.wav",bpm=60)
        )

    }


    //Dinamik müzikleri muzik listesine eklemek için fonksiyon
    fun fetchDynamicMusic() {
        val dbRef = FirebaseDatabase.getInstance().getReference("music_list")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dynamicMusicList = mutableListOf<Music>()
                for (child in snapshot.children) {
                    val music = child.getValue(Music::class.java)
                    if (music != null) {
                        dynamicMusicList.add(music)
                    }
                }
                // Statik liste ile dinamik listeyi birleştiriyoruz.
                val currentList = musicList.value.orEmpty()
                val updatedList = currentList + dynamicMusicList
                musicList.value = updatedList

                Log.d("MusicViewModel", "Firebase'den ${dynamicMusicList.size} adet dinamik müzik çekildi.")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MusicViewModel", "Firebase'den müzik çekerken hata: ${error.message}")
            }
        })
    }






    // Seçilen müzik için LiveData
    private val _selectedMusic = MutableLiveData<Music>()
    val selectedMusic: LiveData<Music> get() = _selectedMusic


    // Seçilen müziği ayarlamak için fonksiyon
    fun setSelectedMusic(music: Music) {
        _selectedMusic.value = music
    }


    /**
     * Gelen id’ye göre müzik listesinden ilgili müzik nesnesini bulup döndürür.
     * Bulunamazsa null döndürür.
     */
    fun getMusicById(id: Int): Music? {
        return musicList.value?.find { it.id == id }
    }
    // Analiz sonuçlarını ayarlamak için fonksiyon

}
