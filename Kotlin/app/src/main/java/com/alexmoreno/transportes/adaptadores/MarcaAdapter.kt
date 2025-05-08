package com.alexmoreno.transportes.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.R
import com.alexmoreno.transportes.modelos.Marca

class MarcaAdapter(
    private val marcas: List<Marca>,
    private val onItemClick: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    class MarcaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNombre: TextView = view.findViewById(R.id.textNombre)
        val textDescripcion: TextView = view.findViewById(R.id.textDescripcion)
        val textAnio: TextView = view.findViewById(R.id.textAnio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marca, parent, false)
        return MarcaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarcaViewHolder, position: Int) {
        val marca = marcas[position]
        holder.textNombre.text = marca.nombre
        holder.textDescripcion.text = marca.descripcion
        holder.textAnio.text = "Fundada en ${marca.anioFundacion}"

        holder.itemView.setOnClickListener {
            onItemClick(marca)
        }
    }

    override fun getItemCount(): Int = marcas.size
}
