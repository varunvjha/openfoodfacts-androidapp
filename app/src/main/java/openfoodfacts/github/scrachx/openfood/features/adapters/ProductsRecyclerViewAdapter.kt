package openfoodfacts.github.scrachx.openfood.features.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.features.productlist.ProductListActivity.Companion.getProductBrandsQuantityDetails
import openfoodfacts.github.scrachx.openfood.models.Product
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient.Companion.localeProductNameField
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper.getLanguage
import openfoodfacts.github.scrachx.openfood.utils.Utils.NO_DRAWABLE_RESOURCE
import openfoodfacts.github.scrachx.openfood.utils.Utils.picassoBuilder
import openfoodfacts.github.scrachx.openfood.utils.getNutriScoreResource

/**
 * @author herau & itchix
 */
class ProductsRecyclerViewAdapter(
        private val products: List<Product?>,
        private val isLowBatteryMode: Boolean,
        private val context: Context
) : RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsListViewHolder {
        val layoutResourceId = if (viewType == VIEW_ITEM) R.layout.products_list_item else R.layout.progressbar_endless_list
        val view = LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
        return if (viewType == VIEW_ITEM) ProductsListViewHolder.ProductViewHolder(view) else ProductsListViewHolder.ProgressViewHolder(view)
    }

    override fun getItemViewType(position: Int) = if (products[position] == null) VIEW_LOAD else VIEW_ITEM

    override fun onBindViewHolder(holder: ProductsListViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (holder !is ProductsListViewHolder.ProductViewHolder) return
        holder.imgSearchProgress.visibility = View.VISIBLE
        val imageSmallUrl = products[position]!!.getImageSmallUrl(getLanguage(context))
        if (imageSmallUrl == null) {
            holder.imgSearchProgress.visibility = View.GONE
        }

        // Load Image if isLowBatteryMode is false
        if (!isLowBatteryMode) {
            picassoBuilder(context)
                    .load(imageSmallUrl)
                    .placeholder(R.drawable.placeholder_thumb)
                    .error(R.drawable.error_image)
                    .fit()
                    .centerCrop()
                    .into(holder.imgProductFront, object : Callback {
                        override fun onSuccess() {
                            holder.imgSearchProgress.visibility = View.GONE
                        }

                        override fun onError(ex: Exception) {
                            holder.imgSearchProgress.visibility = View.GONE
                        }
                    })
        } else {
            Picasso.get().load(R.drawable.placeholder_thumb).into(holder.imgProductFront)
            holder.imgSearchProgress.visibility = View.INVISIBLE
        }
        val product = products[position]
        holder.txtProductName.text = product?.productName ?: ""
        val productNameInLocale = product?.additionalProperties?.get(localeProductNameField) as String?
        if (!productNameInLocale.isNullOrBlank()) {
            holder.txtProductName.text = productNameInLocale
        }
        val brandsQuantityDetails = product?.let { getProductBrandsQuantityDetails(it) }
        val gradeResource = product.getNutriScoreResource()
        if (gradeResource == NO_DRAWABLE_RESOURCE) {
            holder.imgProductGrade.visibility = View.INVISIBLE
        } else {
            holder.imgProductGrade.visibility = View.VISIBLE
            holder.imgProductGrade.setImageResource(gradeResource)
        }
        holder.txtProductDetails.text = brandsQuantityDetails
    }

    fun getProduct(position: Int) = products[position]

    override fun getItemCount() = products.size

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_LOAD = 0
    }

    sealed class ProductsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal class ProgressViewHolder(view: View) : ProductsListViewHolder(view)

        /**
         * Provide a reference to the views for each data item
         * Complex data items may need more than one view per item, and
         * you provide access to all the views for a data item in a view holder
         */
        internal class ProductViewHolder(view: View) : ProductsListViewHolder(view) {
            val txtProductName: TextView = view.findViewById(R.id.nameProduct)
            val txtProductDetails: TextView = view.findViewById(R.id.productDetails)
            val imgProductGrade: ImageView = view.findViewById(R.id.imgGrade)
            val imgProductFront: ImageView = view.findViewById(R.id.imgProduct)
            val imgSearchProgress: ProgressBar = view.findViewById(R.id.searchImgProgressbar)
        }

    }

}


