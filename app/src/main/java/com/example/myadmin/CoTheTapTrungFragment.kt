package com.example.myadmin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapter.ListCoTheTapTrungAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import data.CoTheTapTrung
import data.ExciseStreching
import data.ExciseWarmup
import data.Exercise

class CoTheTapTrungFragmentFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var khamPhaAdapter: ListCoTheTapTrungAdapter
    private lateinit var database: DatabaseReference
    private val khamPhabungList = mutableListOf<CoTheTapTrung>()
    private val khamPhangucList = mutableListOf<CoTheTapTrung>()
    private val khamPhavailungList = mutableListOf<CoTheTapTrung>()
    private val khamPhachanList = mutableListOf<CoTheTapTrung>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_co_the_tap_trung, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        database = FirebaseDatabase.getInstance().getReference("CoTheTapTrung")

        recyclerView.layoutManager = LinearLayoutManager(context)
        khamPhaAdapter = ListCoTheTapTrungAdapter(
            listOf(khamPhabungList, khamPhangucList, khamPhavailungList, khamPhachanList), this::editKhamPha
        )
        recyclerView.adapter = khamPhaAdapter

        loadBung()
        loadNguc()
        loadCanhTayVaVai()
        loadNgucVaMong()

        // Set up the "Add" button (currently empty, can be filled later)
    }





    private fun loadBung() {
        fetchExercisesKhamPha("Bung")
    }
    private fun loadNguc() {
        fetchExercisesKhamPha("Nguc")
    }
    private fun loadCanhTayVaVai() {
        fetchExercisesKhamPha("CanhTayVaVai")
    }
    private fun loadNgucVaMong() {
        fetchExercisesKhamPha("MongVaChan")
    }
    private fun fetchExercisesKhamPha(bophankhampha: String) {
        val coTheTapTrungRef = FirebaseDatabase.getInstance().getReference("CoTheTapTrung/$bophankhampha")

        val baiTapChinhRef = coTheTapTrungRef.child("BaiTapChinh")
        val gianCoRef = coTheTapTrungRef.child("GianCo")
        val khoiDongRef = coTheTapTrungRef.child("KhoiDong")

        val baiTapChinhIds = mutableListOf<Int>()
        val gianCoIds = mutableListOf<Int>()
        val khoiDongIds = mutableListOf<Int>()

        // Bộ đếm để theo dõi số lần tải dữ liệu hoàn tất
        var completedCount = 0

        fun onComplete() {
            completedCount++
            if (completedCount == 3) {
                // Khi tất cả 3 phần dữ liệu được tải, gọi checkAndUpdateList
                checkAndUpdateList(bophankhampha, baiTapChinhIds, gianCoIds, khoiDongIds)
            }
        }

        baiTapChinhRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { baiTapSnapshot ->
                    baiTapChinhIds.add(baiTapSnapshot.getValue(Int::class.java) ?: 0)
                }
                onComplete()  // Gọi khi hoàn tất
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading BaiTapChinh: ${error.message}")
                onComplete()  // Gọi ngay cả khi có lỗi để tránh treo
            }
        })

        gianCoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { baiTapSnapshot ->
                    gianCoIds.add(baiTapSnapshot.getValue(Int::class.java) ?: 0)
                }
                onComplete()  // Gọi khi hoàn tất
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading GianCo: ${error.message}")
                onComplete()  // Gọi ngay cả khi có lỗi để tránh treo
            }
        })

        khoiDongRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { baiTapSnapshot ->
                    khoiDongIds.add(baiTapSnapshot.getValue(Int::class.java) ?: 0)
                }
                onComplete()  // Gọi khi hoàn tất
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading KhoiDong: ${error.message}")
                onComplete()  // Gọi ngay cả khi có lỗi để tránh treo
            }
        })
    }


    private fun checkAndUpdateList(bophankhampha: String, baiTapChinhIds: List<Int>, gianCoIds: List<Int>, khoiDongIds: List<Int>) {
        val cothe = CoTheTapTrung(baiTapChinhIds, gianCoIds, khoiDongIds)
        Log.d("CoTheTapTrungFragment", "cothe: $cothe")
        // Chỉ thêm vào danh sách nếu tất cả dữ liệu đã được tải
        if (bophankhampha == "Bung") {
            khamPhabungList.add(cothe)
        } else if (bophankhampha == "Nguc") {
            khamPhangucList.add(cothe)
        } else if (bophankhampha == "CanhTayVaVai") {
            khamPhavailungList.add(cothe)
        } else {
            khamPhachanList.add(cothe)
        }
        // Notify adapter
        khamPhaAdapter.notifyDataSetChanged()
    }
    private fun showAddEditDialog(coTheTapTrung: CoTheTapTrung?, bophankhampha: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_taptrung, null)
        val multiSelectBaiTapchinh = dialogView.findViewById<MultiAutoCompleteTextView>(R.id.multiSelectBaiTapChinh)
        val multiSelectBaiTapgianco = dialogView.findViewById<MultiAutoCompleteTextView>(R.id.multiSelectGianCo)
        val multiSelectBaiTapkhoidong = dialogView.findViewById<MultiAutoCompleteTextView>(R.id.multiSelectKhoiDong)

        val baiTapChinhMap = mutableMapOf<Int, String>()
        val baiTapGianCoMap = mutableMapOf<Int, String>()
        val baiTapKhoiDongMap = mutableMapOf<Int, String>()

        val baiTapChinhRef = FirebaseDatabase.getInstance().getReference("BaiTap/BaiTapChinh")
        baiTapChinhRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapChinhMap.clear()
                snapshot.children.forEach {
                    val baiTap = it.getValue(Exercise::class.java)
                    if (baiTap != null) {
                        baiTapChinhMap[baiTap.ID] = baiTap.TenBaiTap
                    }
                }

                val baiTapAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    baiTapChinhMap.entries.map { "${it.key} - ${it.value}" } // Hiển thị ID và Tên
                )
                multiSelectBaiTapchinh.setAdapter(baiTapAdapter)
                multiSelectBaiTapchinh.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                if (coTheTapTrung != null) {
                    val selectedBaiTapNames = coTheTapTrung.BaiTapChinh
                        ?.mapNotNull {  id -> baiTapChinhMap[id]?.let { "$id - $it" }  }
                        ?.joinToString(", ")
                    multiSelectBaiTapchinh.setText(selectedBaiTapNames)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading BaiTapChinh: ${error.message}")
            }
        })

        val gianCoRef = FirebaseDatabase.getInstance().getReference("BaiTap/GianCo")
        gianCoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapGianCoMap.clear()
                snapshot.children.forEach {
                    val baiTap = it.getValue(ExciseStreching::class.java)
                    if (baiTap != null) {
                        baiTapGianCoMap[baiTap.ID] = baiTap.TenBaiTap
                    }
                }

                val gianCoAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    baiTapGianCoMap.entries.map { "${it.key} - ${it.value}" } // Hiển thị ID và Tên
                )
                multiSelectBaiTapgianco.setAdapter(gianCoAdapter)
                multiSelectBaiTapgianco.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                if (coTheTapTrung != null) {
                    val selectedGianCoNames = coTheTapTrung.GianCo
                        ?.mapNotNull {  id ->baiTapGianCoMap[id]?.let { "$id - $it" }   }
                        ?.joinToString(", ")
                    multiSelectBaiTapgianco.setText(selectedGianCoNames)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading GianCo: ${error.message}")
            }
        })

        val khoiDongRef = FirebaseDatabase.getInstance().getReference("BaiTap/KhoiDong")
        khoiDongRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapKhoiDongMap.clear()
                snapshot.children.forEach {
                    val baiTap = it.getValue(ExciseWarmup::class.java)
                    if (baiTap != null) {
                        baiTapKhoiDongMap[baiTap.ID] = baiTap.TenBaiTap
                    }
                }

                val khoiDongAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    baiTapKhoiDongMap.entries.map { "${it.key} - ${it.value}" } // Hiển thị ID và Tên
                )
                multiSelectBaiTapkhoidong.setAdapter(khoiDongAdapter)
                multiSelectBaiTapkhoidong.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                if (coTheTapTrung != null) {
                    val selectedKhoiDongNames = coTheTapTrung.KhoiDong
                        ?.mapNotNull {  id -> baiTapKhoiDongMap[id] ?.let { "$id - $it" }   }
                        ?.joinToString(", ")
                    multiSelectBaiTapkhoidong.setText(selectedKhoiDongNames)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading KhoiDong: ${error.message}")
            }
        })

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (coTheTapTrung == null) "Thêm Bài Tập" else "Chỉnh sửa Bài Tập")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                // Lọc ID trực tiếp từ chuỗi nhập vào
                val selectedBaiTapChinhIds = multiSelectBaiTapchinh.text.toString()
                    .split(",")  // Tách theo dấu phẩy
                    .map { it.trim().substringBefore(" -") }  // Lấy phần ID trước dấu " -"
                    .filter { it.isNotEmpty() }  // Loại bỏ chuỗi rỗng
                    .mapNotNull { it.toIntOrNull() }  // Chuyển ID sang số nguyên, bỏ qua nếu không phải số

                val selectedGianCoIds = multiSelectBaiTapgianco.text.toString()
                    .split(",")
                    .map { it.trim().substringBefore(" -") }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { it.toIntOrNull() }

                val selectedKhoiDongIds = multiSelectBaiTapkhoidong.text.toString()
                    .split(",")
                    .map { it.trim().substringBefore(" -") }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { it.toIntOrNull() }

                val updatedCoTheTapTrung = CoTheTapTrung(
                    BaiTapChinh = selectedBaiTapChinhIds,
                    GianCo = selectedGianCoIds,
                    KhoiDong = selectedKhoiDongIds
                )
                FirebaseDatabase.getInstance().getReference("CoTheTapTrung/$bophankhampha")
                    .setValue(updatedCoTheTapTrung)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()
                        fetchExercisesKhamPha("Bung")
                        fetchExercisesKhamPha("Nguc")
                        fetchExercisesKhamPha("CanhTayVaVai")
                        fetchExercisesKhamPha("ChanVaMong")
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lưu thất bại", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()

    }



    private fun editKhamPha(khamPha: CoTheTapTrung, bophankhampha: String) {
        showAddEditDialog(khamPha, bophankhampha)
    }
}

