package com.dayynime.anikukomu.ui.screens.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.core.CloudinaryService
import com.dayynime.anikukomu.data.model.Anime
import com.dayynime.anikukomu.data.repository.AnimeRepository
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePostState(
    val imageUri: Uri? = null,
    val caption: String = "",
    val animeSearchQuery: String = "",
    val searchResults: List<Anime> = emptyList(),
    val selectedAnimes: List<Anime> = emptyList(),
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val isSearchingAnime: Boolean = false,
    val showAddCustomAnimeOption: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class CreatePostViewModel : ViewModel() {

    private val _state = MutableStateFlow(CreatePostState())
    val state: StateFlow<CreatePostState> = _state.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _state.update { it.copy(imageUri = uri, errorMessage = null) }
    }

    fun onCaptionChanged(caption: String) {
        if (caption.length <= 500) {
            _state.update { it.copy(caption = caption) }
        }
    }

    fun onAnimeSearchQueryChanged(query: String) {
        _state.update { it.copy(animeSearchQuery = query) }
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList(), showAddCustomAnimeOption = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSearchingAnime = true) }
            try {
                val results = AnimeRepository.searchAnimes(query)
                val matchesExactResult = results.any { it.title.equals(query, ignoreCase = true) }
                
                _state.update {
                    it.copy(
                        searchResults = results,
                        isSearchingAnime = false,
                        showAddCustomAnimeOption = !matchesExactResult
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSearchingAnime = false,
                        showAddCustomAnimeOption = true
                    )
                }
            }
        }
    }

    fun addAnimeTag(anime: Anime) {
        val current = _state.value.selectedAnimes
        if (current.size >= 5) {
            _state.update { it.copy(errorMessage = "Maksimal tag 5 anime weeb!") }
            return
        }
        if (current.any { it.id == anime.id || it.title.equals(anime.title, ignoreCase = true) }) {
            return
        }
        _state.update {
            it.copy(
                selectedAnimes = current + anime,
                animeSearchQuery = "",
                searchResults = emptyList(),
                showAddCustomAnimeOption = false
            )
        }
    }

    fun removeAnimeTag(anime: Anime) {
        _state.update { s ->
            s.copy(selectedAnimes = s.selectedAnimes.filter { it.id != anime.id })
        }
    }

    fun createCustomAnimeAndTag(title: String, genreSelected: String = "Action") {
        viewModelScope.launch {
            _state.update { it.copy(isSearchingAnime = true) }
            try {
                val newAnime = AnimeRepository.addAnime(title, listOf(genreSelected))
                if (newAnime != null) {
                    addAnimeTag(newAnime)
                } else {
                    _state.update { it.copy(errorMessage = "Gagal membuat anime baru") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Error membuat anime: ${e.localizedMessage}") }
            } finally {
                _state.update { it.copy(isSearchingAnime = false) }
            }
        }
    }

    fun createPost(context: Context) {
        val uri = _state.value.imageUri
        if (uri == null) {
            _state.update { it.copy(errorMessage = "Pilih gambar dulu ya weeb!") }
            return
        }

        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(errorMessage = "Silakan login terlebih dahulu untuk posting") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, errorMessage = null) }
            try {
                // 1. Upload to Cloudinary
                val uploadResult = CloudinaryService.uploadImage(context, uri)

                // 2. Submit Post Metadatas to Supabase
                val animeIds = _state.value.selectedAnimes.mapNotNull { it.id }
                val submitSuccess = PostRepository.createPost(
                    userId = currentUserId,
                    caption = _state.value.caption,
                    imageUrl = uploadResult.secureUrl,
                    imagePublicId = uploadResult.publicId,
                    animeIds = animeIds
                )

                if (submitSuccess) {
                    _state.update { it.copy(isSuccess = true, isUploading = false) }
                } else {
                    _state.update { it.copy(isUploading = false, errorMessage = "Gagal mendaftarkan postingan ke Supabase") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isUploading = false, errorMessage = "Gagal mengupload: ${e.localizedMessage}") }
            }
        }
    }

    fun resetState() {
        _state.value = CreatePostState()
    }
}
