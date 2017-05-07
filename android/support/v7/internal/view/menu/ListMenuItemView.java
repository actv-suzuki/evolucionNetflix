// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v7.internal.view.menu;

import android.widget.CompoundButton;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.support.v7.appcompat.R$id;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.appcompat.R$layout;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R$styleable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ListMenuItemView extends LinearLayout implements aa
{
    private m a;
    private ImageView b;
    private RadioButton c;
    private TextView d;
    private CheckBox e;
    private TextView f;
    private Drawable g;
    private int h;
    private Context i;
    private boolean j;
    private int k;
    private Context l;
    private LayoutInflater m;
    private boolean n;
    
    public ListMenuItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ListMenuItemView(final Context context, final AttributeSet set, final int n) {
        super(context, set);
        this.l = context;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.MenuView, n, 0);
        this.g = obtainStyledAttributes.getDrawable(R$styleable.MenuView_android_itemBackground);
        this.h = obtainStyledAttributes.getResourceId(R$styleable.MenuView_android_itemTextAppearance, -1);
        this.j = obtainStyledAttributes.getBoolean(R$styleable.MenuView_preserveIconSpacing, false);
        this.i = context;
        obtainStyledAttributes.recycle();
    }
    
    private void c() {
        this.addView((View)(this.b = (ImageView)this.f().inflate(R$layout.abc_list_menu_item_icon, (ViewGroup)this, false)), 0);
    }
    
    private void d() {
        this.addView((View)(this.c = (RadioButton)this.f().inflate(R$layout.abc_list_menu_item_radio, (ViewGroup)this, false)));
    }
    
    private void e() {
        this.addView((View)(this.e = (CheckBox)this.f().inflate(R$layout.abc_list_menu_item_checkbox, (ViewGroup)this, false)));
    }
    
    private LayoutInflater f() {
        if (this.m == null) {
            this.m = LayoutInflater.from(this.l);
        }
        return this.m;
    }
    
    public m a() {
        return this.a;
    }
    
    public void a(Drawable imageDrawable) {
        boolean b;
        if (this.a.g() || this.n) {
            b = true;
        }
        else {
            b = false;
        }
        if ((b || this.j) && (this.b != null || imageDrawable != null || this.j)) {
            if (this.b == null) {
                this.c();
            }
            if (imageDrawable == null && !this.j) {
                this.b.setVisibility(8);
                return;
            }
            final ImageView b2 = this.b;
            if (!b) {
                imageDrawable = null;
            }
            b2.setImageDrawable(imageDrawable);
            if (this.b.getVisibility() != 0) {
                this.b.setVisibility(0);
            }
        }
    }
    
    public void a(final m a, int n) {
        this.a = a;
        this.k = n;
        if (a.isVisible()) {
            n = 0;
        }
        else {
            n = 8;
        }
        this.setVisibility(n);
        this.a(a.a(this));
        this.b(a.isCheckable());
        this.a(a.e(), a.c());
        this.a(a.getIcon());
        this.setEnabled(a.isEnabled());
    }
    
    public void a(final CharSequence text) {
        if (text != null) {
            this.d.setText(text);
            if (this.d.getVisibility() != 0) {
                this.d.setVisibility(0);
            }
        }
        else if (this.d.getVisibility() != 8) {
            this.d.setVisibility(8);
        }
    }
    
    public void a(final boolean b) {
        this.n = b;
        this.j = b;
    }
    
    public void a(final boolean b, final char c) {
        int visibility;
        if (b && this.a.e()) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        if (visibility == 0) {
            this.f.setText((CharSequence)this.a.d());
        }
        if (this.f.getVisibility() != visibility) {
            this.f.setVisibility(visibility);
        }
    }
    
    public void b(final boolean b) {
        if (b || this.c != null || this.e != null) {
            Object o;
            Object o2;
            if (this.a.f()) {
                if (this.c == null) {
                    this.d();
                }
                o = this.c;
                o2 = this.e;
            }
            else {
                if (this.e == null) {
                    this.e();
                }
                o = this.e;
                o2 = this.c;
            }
            if (b) {
                ((CompoundButton)o).setChecked(this.a.isChecked());
                int visibility;
                if (b) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                if (((CompoundButton)o).getVisibility() != visibility) {
                    ((CompoundButton)o).setVisibility(visibility);
                }
                if (o2 != null && ((CompoundButton)o2).getVisibility() != 8) {
                    ((CompoundButton)o2).setVisibility(8);
                }
            }
            else {
                if (this.e != null) {
                    this.e.setVisibility(8);
                }
                if (this.c != null) {
                    this.c.setVisibility(8);
                }
            }
        }
    }
    
    public boolean b() {
        return false;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.setBackgroundDrawable(this.g);
        this.d = (TextView)this.findViewById(R$id.title);
        if (this.h != -1) {
            this.d.setTextAppearance(this.i, this.h);
        }
        this.f = (TextView)this.findViewById(R$id.shortcut);
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (this.b != null && this.j) {
            final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)this.b.getLayoutParams();
            if (layoutParams.height > 0 && linearLayout$LayoutParams.width <= 0) {
                linearLayout$LayoutParams.width = layoutParams.height;
            }
        }
        super.onMeasure(n, n2);
    }
}
