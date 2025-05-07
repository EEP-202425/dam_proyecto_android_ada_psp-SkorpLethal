package com.alexmoreno.transportes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.models.Modelo
import com.alexmoreno.transportes.R

class ModeloAdapter(
    private val modelos: List<Modelo>,
    private val onItemClick: (Modelo) -> Unit
) : RecyclerView.Adapter<ModeloAdapter.ModeloViewHolder>() {

    class ModeloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.textNombreModelo)
        val descripcion: TextView = view.findViewById(R.id.textDescripcionModelo)
        val precioText: TextView = view.findViewById(R.id.textPrecioBaseModelo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modelo, parent, false)
        return ModeloViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModeloViewHolder, position: Int) {
        val modelo = modelos[position]
        holder.nombre.text = modelo.nombre
        holder.descripcion.text = modelo.descripcion
        holder.precioText.text = "Precio Base: ${modelo.precioBase.toString()}";

        holder.itemView.setOnClickListener {
            onItemClick(modelo)
        }
    }

    override fun getItemCount(): Int = modelos.size
}
