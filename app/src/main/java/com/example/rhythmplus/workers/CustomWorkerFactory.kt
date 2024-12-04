//package com.example.rhythmplus.workers
//
//import android.content.Context
//import androidx.work.ListenableWorker
//import androidx.work.WorkerFactory
//import androidx.work.WorkerParameters
//import com.example.rhythmplus.data.DataRepository
//
//class CustomWorkerFactory(
//    private val dataRepo: DataRepository
//) : WorkerFactory() {
//    override fun createWorker(
//        context: Context,
//        workerClassName: String,
//        workerParameters: WorkerParameters
//    ): ListenableWorker? {
//        return when (workerClassName) {
//            FetchAndUpdateDBWorker::class.java.name -> FetchAndUpdateDBWorker(context, workerParameters, dataRepo)
//            else -> null
//        }
//    }
//}