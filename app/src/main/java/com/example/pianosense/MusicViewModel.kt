import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pianosense.Music

class MusicViewModel : ViewModel() {
    private val _selectedMusic = MutableLiveData<Music?>()
    val selectedMusic: LiveData<Music?> get() = _selectedMusic

    fun setSelectedMusic(music: Music) {
        _selectedMusic.value = music
    }
}
