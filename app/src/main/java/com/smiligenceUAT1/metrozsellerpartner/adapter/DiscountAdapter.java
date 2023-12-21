package com.smiligenceUAT1.metrozsellerpartner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceUAT1.metrozsellerpartner.R;
import com.smiligenceUAT1.metrozsellerpartner.bean.Discount;

import java.util.List;

public class DiscountAdapter extends BaseAdapter {

    private Context mcontext;
    private List<Discount> discountList;
    LayoutInflater inflater;

    public DiscountAdapter(Context context, List<Discount> listDiscount) {
        mcontext = context;
        discountList = listDiscount;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images;
        TextView t_name, t_price_percent,t_minimumamount;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.discount_adapter_grid, parent, false);
            holder.t_name = row.findViewById(R.id.t_name);
            holder.t_price_percent = row.findViewById(R.id.t_price_percent);
            holder.t_minimumamount=row.findViewById(R.id.minimum_discount);

            holder.images = (ImageView) row.findViewById(R.id.image1);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Discount newDiscount = discountList.get(position);

        if (newDiscount.getDiscountType().equals("Bill Discount")) {

            if (newDiscount.getDiscountStatus().equals("Active")) {
                holder.t_name.setText(newDiscount.getDiscountName());

                if (newDiscount.getDiscountPrice() == (null)||"".equals(newDiscount.getDiscountPrice())) {
                    holder.  t_price_percent.setText(newDiscount.getDiscountPercentageValue()+"%");
                    holder.t_minimumamount.setText(" Max dis ₹"+newDiscount.getMaxAmountForDiscount());

                } else if (newDiscount.getDiscountPercentageValue() == (null)||"".equals(newDiscount.getDiscountPercentageValue())) {
                    holder.  t_price_percent.setText("₹"+newDiscount.getDiscountPrice());

                    holder.t_minimumamount.setText(" Min bill ₹"+newDiscount.getMinmumBillAmount());
                    holder.t_price_percent.setVisibility(View.VISIBLE);

                }
            }else if(newDiscount.getDiscountStatus().equals("Inactive")) {
                holder.t_name.setText(newDiscount.getDiscountName());
                if (newDiscount.getDiscountPrice() == (null)||"".equals(newDiscount.getDiscountPrice())) {
                    holder.  t_price_percent.setText(newDiscount.getDiscountPercentageValue()+"%");
                    holder.t_minimumamount.setText(" Max dis ₹"+newDiscount.getMaxAmountForDiscount());
                } else if (newDiscount.getDiscountPercentageValue() == (null)||"".equals(newDiscount.getDiscountPercentageValue())) {
                    holder.  t_price_percent.setText("₹"+newDiscount.getDiscountPrice());

                    holder.t_minimumamount.setText(" Min bill ₹"+newDiscount.getMinmumBillAmount());
                    holder.t_price_percent.setVisibility(View.VISIBLE);
                }
                RequestOptions requestOptions = new RequestOptions ();
                requestOptions.placeholder ( R.mipmap.ic_launcher );
                requestOptions.error ( R.mipmap.ic_launcher );
                Glide.with ( mcontext )
                        .setDefaultRequestOptions ( requestOptions )
                        .load ( newDiscount.getDiscountImage() ).fitCenter ().into ( holder.images );
                ColorMatrix matrix = new ColorMatrix ();
                matrix.setSaturation ( 0 );
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter ( matrix );
                holder.images.setColorFilter ( filter );
            }
        }
        if (newDiscount.getDiscountType().equals("Free Offers")) {
            if(newDiscount.getDiscountStatus().equals("Active")){
                holder.t_name.setText(newDiscount.getDiscountName());
            }else if(newDiscount.getDiscountStatus().equals("Inactive")){
                holder.t_name.setText(newDiscount.getDiscountName());
                holder.images.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(newDiscount.getDiscountImage()).fitCenter().into(holder.images);
        return row;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}