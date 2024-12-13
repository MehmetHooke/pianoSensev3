import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pianosense.ComparisonResult
import com.example.pianosense.Music
import com.example.pianosense.NoteInfo
import com.example.pianosense.R

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
        // Varsayılan müzik listesini tanımlayın
        musicList.value = listOf(
            Music(1, "Valse", "Evgeny Grinko", R.drawable.avatar, "originalMusic1.wav"),
            Music(2, "Moonlight Sonata", "Beethoven", R.drawable.avatar, "originalMusic1.wav"),
            Music(3, "Für Elise", "Beethoven", R.drawable.avatar, "originalMusic1.wav")
        )
    }

    // Seçilen müzik için LiveData
    private val _selectedMusic = MutableLiveData<Music>()
    val selectedMusic: LiveData<Music> get() = _selectedMusic

    // Analiz sonuçları için LiveData


    // Seçilen müziği ayarlamak için fonksiyon
    fun setSelectedMusic(music: Music) {
        _selectedMusic.value = music
    }

    // Analiz sonuçlarını ayarlamak için fonksiyon

}
