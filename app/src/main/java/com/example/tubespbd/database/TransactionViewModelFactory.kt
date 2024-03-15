//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.tubespbd.database.TransactionRepository
//import com.example.tubespbd.database.TransactionViewModel
//
//class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return TransactionViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}