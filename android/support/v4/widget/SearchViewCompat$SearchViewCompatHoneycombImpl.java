// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.widget;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;

class SearchViewCompat$SearchViewCompatHoneycombImpl extends SearchViewCompat$SearchViewCompatStubImpl
{
    protected void checkIfLegalArg(final View view) {
        SearchViewCompatHoneycomb.checkIfLegalArg(view);
    }
    
    @Override
    public CharSequence getQuery(final View view) {
        this.checkIfLegalArg(view);
        return SearchViewCompatHoneycomb.getQuery(view);
    }
    
    @Override
    public boolean isIconified(final View view) {
        this.checkIfLegalArg(view);
        return SearchViewCompatHoneycomb.isIconified(view);
    }
    
    @Override
    public boolean isQueryRefinementEnabled(final View view) {
        this.checkIfLegalArg(view);
        return SearchViewCompatHoneycomb.isQueryRefinementEnabled(view);
    }
    
    @Override
    public boolean isSubmitButtonEnabled(final View view) {
        this.checkIfLegalArg(view);
        return SearchViewCompatHoneycomb.isSubmitButtonEnabled(view);
    }
    
    @Override
    public Object newOnCloseListener(final SearchViewCompat$OnCloseListener searchViewCompat$OnCloseListener) {
        return SearchViewCompatHoneycomb.newOnCloseListener(new SearchViewCompat$SearchViewCompatHoneycombImpl$2(this, searchViewCompat$OnCloseListener));
    }
    
    @Override
    public Object newOnQueryTextListener(final SearchViewCompat$OnQueryTextListener searchViewCompat$OnQueryTextListener) {
        return SearchViewCompatHoneycomb.newOnQueryTextListener(new SearchViewCompat$SearchViewCompatHoneycombImpl$1(this, searchViewCompat$OnQueryTextListener));
    }
    
    @Override
    public View newSearchView(final Context context) {
        return SearchViewCompatHoneycomb.newSearchView(context);
    }
    
    @Override
    public void setIconified(final View view, final boolean b) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setIconified(view, b);
    }
    
    @Override
    public void setMaxWidth(final View view, final int n) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setMaxWidth(view, n);
    }
    
    @Override
    public void setOnCloseListener(final View view, final SearchViewCompat$OnCloseListener searchViewCompat$OnCloseListener) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setOnCloseListener(view, this.newOnCloseListener(searchViewCompat$OnCloseListener));
    }
    
    @Override
    public void setOnQueryTextListener(final View view, final SearchViewCompat$OnQueryTextListener searchViewCompat$OnQueryTextListener) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setOnQueryTextListener(view, this.newOnQueryTextListener(searchViewCompat$OnQueryTextListener));
    }
    
    @Override
    public void setQuery(final View view, final CharSequence charSequence, final boolean b) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setQuery(view, charSequence, b);
    }
    
    @Override
    public void setQueryHint(final View view, final CharSequence charSequence) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setQueryHint(view, charSequence);
    }
    
    @Override
    public void setQueryRefinementEnabled(final View view, final boolean b) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setQueryRefinementEnabled(view, b);
    }
    
    @Override
    public void setSearchableInfo(final View view, final ComponentName componentName) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setSearchableInfo(view, componentName);
    }
    
    @Override
    public void setSubmitButtonEnabled(final View view, final boolean b) {
        this.checkIfLegalArg(view);
        SearchViewCompatHoneycomb.setSubmitButtonEnabled(view, b);
    }
}
