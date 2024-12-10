import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pianosense.Music
import com.example.pianosense.NoteComparison
import com.example.pianosense.NoteInfo

class MusicViewModel : ViewModel() {

    private val _selectedMusic = MutableLiveData<Music>()
    val selectedMusic: LiveData<Music> get() = _selectedMusic

    private val _analysisResults = MutableLiveData<List<NoteInfo>>() // Doğrudan NoteInfo türü
    val analysisResults: LiveData<List<NoteInfo>> get() = _analysisResults

    fun setSelectedMusic(music: Music) {
        _selectedMusic.value = music
    }

    fun setAnalysisResults(results: List<NoteInfo>) {
        // Artık dönüşüme gerek yok; NoteInfo türünü doğrudan alıyoruz
        _analysisResults.value = results
    }
}


