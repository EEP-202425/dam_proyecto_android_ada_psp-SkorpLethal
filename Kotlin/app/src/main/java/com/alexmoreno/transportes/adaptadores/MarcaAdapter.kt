package com.alexmoreno.transportes.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.R
import com.alexmoreno.transportes.modelos.Marca
import com.google.android.material.button.MaterialButton

class MarcaAdapter(
    private val marcas: MutableList<Marca>,
    private val onItemClick: (Marca) -> Unit,
    private val onEditarClick: (Marca) -> Unit,
    private val onEliminarClick: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    class MarcaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNombre: TextView = view.findViewById(R.id.textNombre)
        val textDescripcion: TextView = view.findViewById(R.id.textDescripcion)
        val textAnio: TextView = view.findViewById(R.id.textAnio)
        val textNumModelos: TextView = view.findViewById(R.id.textNumModelos)

        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marca, parent, false)
        return MarcaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarcaViewHolder, position: Int) {
        val marca = marcas[position]
        holder.textNombre.text = marca.nombre
        holder.textDescripcion.text = if (marca.descripcion.length > 100)
            marca.descripcion.take(100) + "..."
        else
            marca.descripcion
        holder.textAnio.text = "Fundada en el año : ${marca.anioFundacion}"
        holder.textNumModelos.text = "Modelos : ${marca.modelos.count()}"
        holder.itemView.setOnClickListener { onItemClick(marca) }
        holder.btnEditar.setOnClickListener { onEditarClick(marca) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(marca) }
    }

    override fun getItemCount(): Int = marcas.size

    fun actualizarLista(nuevaLista: List<Marca>) {
        marcas.clear()
        marcas.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
