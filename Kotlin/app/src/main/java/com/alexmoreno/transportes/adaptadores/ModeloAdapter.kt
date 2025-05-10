package com.alexmoreno.transportes.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import com.alexmoreno.transportes.modelos.Modelo
import com.alexmoreno.transportes.R
import com.alexmoreno.transportes.modelos.Marca

class ModeloAdapter(
    private val modelos: MutableList<Modelo>,
    private val modoConsulta: Boolean,
    private val onItemClick: (Modelo) -> Unit,
    private val onEditarClick: (Modelo) -> Unit,
    private val onEliminarClick: (Modelo) -> Unit
) : RecyclerView.Adapter<ModeloAdapter.ModeloViewHolder>() {

    class ModeloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.textNombreModelo)
        val descripcion: TextView = view.findViewById(R.id.textDescripcionModelo)
        val precioBaseText: TextView = view.findViewById(R.id.textPrecioBaseModelo)
        val precioTotalText: TextView = view.findViewById(R.id.textPrecioTotalModelo)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditarModelo)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminarModelo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modelo, parent, false)
        return ModeloViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModeloViewHolder, position: Int) {
        val modelo = modelos[position]
        holder.nombre.text = modelo.nombre
        holder.descripcion.text = if (modelo.descripcion.length > 100)
            modelo.descripcion.take(100) + "..."
        else
            modelo.descripcion
        holder.precioBaseText.text = "Precio base: ${modelo.precioBase} €"
        holder.precioTotalText.text = "Precio total: ${modelo.precioTotal} €"

        holder.itemView.setOnClickListener { onItemClick(modelo) }
        holder.btnEditar.setOnClickListener { onEditarClick(modelo) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(modelo) }

        if (modoConsulta) {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        } else {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = modelos.size

    fun actualizarLista(nuevaLista: List<Modelo>) {
        modelos.clear()
        modelos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}