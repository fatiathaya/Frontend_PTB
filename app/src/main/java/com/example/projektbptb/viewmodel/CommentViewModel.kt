package com.example.projektbptb.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektbptb.data.model.Comment
import com.example.projektbptb.data.network.AuthRepository
import com.example.projektbptb.data.network.CommentResponse
import com.example.projektbptb.data.repository.CommentRepository
import kotlinx.coroutines.launch

class CommentViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val commentRepository = CommentRepository()
    
    val comments = mutableStateListOf<Comment>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    
    fun loadComments(productId: Int) {
        val token = authRepository.getToken()
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            commentRepository.getComments(productId, token)
                .onSuccess { commentResponses ->
                    comments.clear()
                    comments.addAll(commentResponses.map { it.toComment() })
                    isLoading.value = false
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal memuat komentar"
                    isLoading.value = false
                }
        }
    }
    
    fun createComment(productId: Int, commentText: String, parentCommentId: Int? = null, onSuccess: () -> Unit = {}) {
        val token = authRepository.getToken() ?: run {
            errorMessage.value = "Silakan login untuk berkomentar"
            return
        }
        
        if (productId <= 0) {
            errorMessage.value = "ID produk tidak valid"
            return
        }
        
        if (commentText.isBlank()) {
            errorMessage.value = "Komentar tidak boleh kosong"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            commentRepository.createComment(productId, token, commentText.trim(), parentCommentId)
                .onSuccess { commentResponse ->
                    val newComment = commentResponse.toComment()
                    if (parentCommentId != null) {
                        // This is a reply - find parent and add reply
                        val parentIndex = comments.indexOfFirst { it.id == parentCommentId.toString() }
                        if (parentIndex != -1) {
                            val parent = comments[parentIndex]
                            val updatedParent = parent.copy(replies = parent.replies + newComment)
                            comments[parentIndex] = updatedParent
                        } else {
                            // Parent not found in current list, reload comments
                            loadComments(productId)
                        }
                    } else {
                        // This is a top-level comment
                        comments.add(0, newComment)
                    }
                    isLoading.value = false
                    errorMessage.value = null // Clear any previous errors
                    onSuccess()
                }
                .onFailure { exception ->
                    android.util.Log.e("CommentViewModel", "Failed to create comment: ${exception.message}", exception)
                    errorMessage.value = exception.message ?: "Gagal membuat komentar"
                    isLoading.value = false
                }
        }
    }
    
    fun updateComment(commentId: Int, commentText: String, onSuccess: () -> Unit = {}) {
        val token = authRepository.getToken() ?: run {
            errorMessage.value = "Silakan login untuk mengedit komentar"
            return
        }
        
        if (commentText.isBlank()) {
            errorMessage.value = "Komentar tidak boleh kosong"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            commentRepository.updateComment(commentId, token, commentText)
                .onSuccess { commentResponse ->
                    val updatedComment = commentResponse.toComment()
                    val index = comments.indexOfFirst { it.id == commentId.toString() }
                    if (index != -1) {
                        comments[index] = updatedComment
                    }
                    isLoading.value = false
                    onSuccess()
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal mengupdate komentar"
                    isLoading.value = false
                }
        }
    }
    
    fun deleteComment(commentId: Int, onSuccess: () -> Unit = {}) {
        val token = authRepository.getToken() ?: run {
            errorMessage.value = "Silakan login untuk menghapus komentar"
            return
        }
        
        isLoading.value = true
        errorMessage.value = null
        
        viewModelScope.launch {
            commentRepository.deleteComment(commentId, token)
                .onSuccess {
                    comments.removeAll { it.id == commentId.toString() }
                    isLoading.value = false
                    onSuccess()
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Gagal menghapus komentar"
                    isLoading.value = false
                }
        }
    }
    
    fun getCurrentUserId(): Int? {
        // Get user ID from auth repository if available
        // This can be implemented based on how user data is stored
        return null
    }
    
    private fun CommentResponse.toComment(): Comment {
        return Comment(
            id = id.toString(),
            productId = product_id,
            userId = user_id,
            userName = user_name,
            userUsername = user_username,
            userProfileImage = user_profile_image,
            commentText = comment,
            parentCommentId = parent_comment_id,
            replies = replies?.map { it.toComment() } ?: emptyList(),
            timestamp = formatTimestamp(created_at),
            updatedAt = updated_at
        )
    }
    
    private fun formatTimestamp(timestamp: String): String {
        return try {
            // Simple formatting - can be enhanced with proper date formatting
            timestamp
        } catch (e: Exception) {
            timestamp
        }
    }
}

