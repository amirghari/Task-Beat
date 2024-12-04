//package com.example.rhythmplus.workers
//
//import android.content.Context
//import android.util.Log
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.rhythmplus.data.DataRepository
//import com.example.rhythmplus.model.ParliamentMember
//import com.example.rhythmplus.model.ParliamentMemberLocal
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.async
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.withContext
//
//class FetchAndUpdateDBWorker(
//    context: Context,
//    params: WorkerParameters,
//    private val dataRepo: DataRepository
//): CoroutineWorker(context, params) {
//    private lateinit var responsePM: List<ParliamentMember>
//    private lateinit var responsePME: List<ParliamentMemberExtra>
//    private lateinit var responsePair: List<Pair<ParliamentMember, ParliamentMemberExtra?>>
//
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        try {
//            val fetchMembersDeferred = async { dataRepo.fetchParliamentMembersData() }
//            val fetchExtraDataDeferred = async { dataRepo.fetchParliamentMembersExtraData() }
//
//            responsePM = fetchMembersDeferred.await().toMutableList()
//            responsePME = fetchExtraDataDeferred.await().toMutableList()
//        }
//        catch (e: Exception) {
//            Log.e("DBG", "Error fetching data: ${e.message}")
//            return@withContext handleRetryOrFailure(e)
//        }
//
//        val pmeMap = responsePME.associateBy { it.hetekaId }
//
//        responsePair = responsePM.map { member ->
//            pmeMap[member.hetekaId]?.let { extra ->
//                Pair(member, extra)
//            } ?: Pair(member, ParliamentMemberExtra(member.hetekaId, null, 0, ""))
//        }
//
//        Log.d("DBG", "Pair: $responsePair")
//
//        responsePair.forEach { data ->
//            try {
//                dataRepo.addParliamentMember(data.first)
//                if (data.second != null) dataRepo.addParliamentMemberExtra(data.second!!)
//
//                val existingEntry = dataRepo.getMemberLocalWithId(data.first.hetekaId).firstOrNull()
//                if (existingEntry == null) {
//                    dataRepo.addParliamentLocal(
//                        ParliamentMemberLocal(
//                            hetekaId = data.first.hetekaId,
//                            favorite = false,
//                            note = null
//                        )
//                    )
//                }
//            }
//            catch (e: Exception) {
//                Log.e("DBG", "Failed to add entries: $data | Error: ${e.message}")
//            }
//        }
//
//        Log.d("DBG", "SUCCESS: Fetched and updated data")
//        return@withContext Result.success()
//    }
//
//    private fun handleRetryOrFailure(e: Exception): Result {
//        return if (runAttemptCount < 3) {
//            Log.e("DBG", "Retry attempt: $runAttemptCount | Error in work ${this.javaClass.name}: ${e.message}")
//            Result.retry()
//        }
//        else {
//            Log.e("DBG", "Exceeded retries. Work failed | Error in work ${this.javaClass.name}: ${e.message}")
//            Result.failure()
//        }
//    }
//}