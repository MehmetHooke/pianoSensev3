package com.example.pianosense

import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryAdapter(
    private val historyList: MutableList<HistoryItem>,  // MutableList olarak tanımlandı
    private val staticMusicList: List<Music>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // Açılmış (expanded) pozisyonları takip etmek için
    private val expandedPositions = mutableSetOf<Int>()

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicNameTextView: TextView = itemView.findViewById(R.id.historyMusicName)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.historyDateTime)
        val musicImageView: ImageView = itemView.findViewById(R.id.historyMusicImage)
        val detailsLayout: View = itemView.findViewById(R.id.detailsLayout)
        val correctNotesTextView: TextView = itemView.findViewById(R.id.correctNotesTextView)
        val correctNoteListTextView: TextView = itemView.findViewById(R.id.correctNoteListTextView)
        val wrongNotesTextView: TextView = itemView.findViewById(R.id.wrongNotesTextView)
        val wrongNoteListTextView: TextView = itemView.findViewById(R.id.wrongNoteListTextView)
        val binButton: Button = itemView.findViewById(R.id.bin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        val musicImageView: ImageView = view.findViewById(R.id.historyMusicImage)
        // Düşük API seviyelerinde kenar yuvarlatma için
        musicImageView.post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                musicImageView.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val radius = view.width * 0.15f
                        outline.setRoundRect(0, 0, view.width, view.height, radius)
                    }
                }
                musicImageView.clipToOutline = true
            }
        }
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.musicNameTextView.text = item.music_name
        holder.dateTimeTextView.text = item.date_time

        if (item.music_image_url.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.music_image_url)
                .transform(RoundedCorners(16))
                .into(holder.musicImageView)
        } else {
            val musicItem = staticMusicList.find { it.title == item.music_name }
            if (musicItem != null && musicItem.imageResId != 0) {
                holder.musicImageView.setImageResource(musicItem.imageResId)
            } else {
                holder.musicImageView.setImageResource(R.drawable.avatar)
            }
        }

        // Doğru notaları liste şeklinde göster
        holder.correctNotesTextView.text = "Doğru Sayısı: ${item.correct_notes}"
        holder.correctNoteListTextView.text =
            if (item.correct_note_list.isNotEmpty()) item.correct_note_list.joinToString("\n") else "-"
        // Yanlış notaların sayısını ve listesini göster
        holder.wrongNotesTextView.text = "Yanlış Sayısı: ${item.wrong_notes}"
        holder.wrongNoteListTextView.text =
            if (item.wrong_note_list.isNotEmpty()) item.wrong_note_list.joinToString("\n") else "-"

        holder.detailsLayout.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
            if (expandedPositions.contains(adapterPosition)) {
                expandedPositions.remove(adapterPosition)
            } else {
                expandedPositions.add(adapterPosition)
            }
            notifyItemChanged(adapterPosition)
        }

        // Kullanıcının rolünü kontrol et
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("rol")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rol = snapshot.getValue(String::class.java)
                    if (rol == "ogretmen") {
                        holder.binButton.visibility = View.GONE
                    } else {
                        holder.binButton.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    holder.binButton.visibility = View.VISIBLE  // hata olursa göster
                }
            })
        } else {
            holder.binButton.visibility = View.VISIBLE // login olmamışsa yine göster
        }


        // Bin butonuna tıklama listener'ı ekliyoruz
        holder.binButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                .setTitle("Silme Onayı")
                .setMessage("Silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet") { dialog, _ ->
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val userId = currentUser.uid
                        val historyRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(userId)
                            .child("result_history")
                        // Date_time ve music_name'e göre sorgulama
                        val query = historyRef.orderByChild("date_time").equalTo(item.date_time)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var deleted = false
                                for (child in snapshot.children) {
                                    val historyItem = child.getValue(HistoryItem::class.java)
                                    if (historyItem != null && historyItem.music_name == item.music_name) {
                                        child.ref.removeValue().addOnSuccessListener {
                                            Toast.makeText(
                                                holder.itemView.context,
                                                "İtem Silindi",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val adapterPos = holder.adapterPosition
                                            if (adapterPos != RecyclerView.NO_POSITION) {
                                                historyList.removeAt(adapterPos)
                                                notifyItemRemoved(adapterPos)
                                            }
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                holder.itemView.context,
                                                "Öğe Silinirken Problemle Karşılaşıldı",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        deleted = true
                                        break
                                    }
                                }
                                if (!deleted) {
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "Hata 301",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Error: ${error.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        Toast.makeText(holder.itemView.context, "Kullanıcı Girişi Yok", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Hayır") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun getItemCount(): Int = historyList.size
}
