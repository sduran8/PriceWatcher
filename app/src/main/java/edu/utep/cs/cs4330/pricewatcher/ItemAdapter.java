package edu.utep.cs.cs4330.pricewatcher;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

//--DATABASE----------------------------------------------------------------------------------------

    private ItemClickListener listener;
    public interface ItemClickListener { void itemClicked(Item item); }
    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

//--CONSTRUCTORS------------------------------------------------------------------------------------

    public ItemAdapter(Context context, int resourceId, List<Item> items) {
        super(context, resourceId, items);
    }

//--METHODS-----------------------------------------------------------------------------------------

    //Adds the item to the content_list for display
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.watched_item, parent, false);
        }
        Item product = getItem(position);
        TextView textView = convertView.findViewById(R.id.item);
        textView.setText(Html.fromHtml("<b>Item: </b>" + product.getItem() +
                                              "<br/> <b>Current Price: </b>" + "$" + product.getCurrentPrice() +
                                              "<br/> <b>Initial Price: </b>" + "$" + product.getInitialPrice() +
                                              "<br/> <b>Price Change: </b>" + product.getPriceChange() + "%" +
                                              "<br/> <b>URL: </b>"+ product.getUrl()));
        return convertView;
    }
}