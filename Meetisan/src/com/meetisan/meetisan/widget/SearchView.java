/**
 * 
 */
package com.meetisan.meetisan.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.SystemHelper;

/**
 * Initialize search panels and fields For normal work you must have
 * $(tag)_page1 and $(tag)_page2 group view, where For example:
 * 
 * <pre>
 * {@code
 * <!-- Page 2 -->
 * <ScrollView
 * 	android:tag="@string/tag_page1"
 * 	android:layout_width="fill_parent"
 * 	android:layout_height="fill_parent"/>
 * <!-- Page 2 -->
 * <com.freshdirect.android.widget.SearchListView
 * 	android:tag="@string/tag_page2"
 * 	android:layout_width="fill_parent"
 * 	android:layout_height="fill_parent"/>
 * }
 * </pre>
 * 
 * @author Nikolay Moskvin <moskvin@j2ee.ru>
 * @date 23.10.2010
 * */
public class SearchView extends RelativeLayout implements EditText.OnTouchListener, TextWatcher,
		EditText.OnEditorActionListener, EditText.OnFocusChangeListener, OnScrollListener {

	private Context context;

	private EditText searchEditText;
	private TextView searchEmptyText;
	private Button buttonCancel;

	private SearchListener searchListener;
	private boolean enabledSearchPanel = false;
	private boolean applicationSetText = false;

	public interface SearchListener {
		void onAutoSuggestion(String query);

		void onClickSearchResult(String query);

		void onClear();
	}

	/**
	 * Constructor with style
	 * 
	 * @param context
	 *            is current context of activity
	 * @param attrs
	 *            is set of attributes
	 * @param defStyle
	 *            is concrete style
	 */
	public SearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Constructor with attributes
	 * 
	 * @param context
	 *            is current context of activity
	 * @param attrs
	 *            is set of attributes
	 */
	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Simple constructor
	 * 
	 * @param context
	 *            is current context of activity
	 */
	public SearchView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Inflate search_panel.xml and configuration all view
	 * 
	 * @param context
	 *            is current context of activity
	 */
	private void init(Context context) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.search_panel, this, true);

		buttonCancel = (Button) view.findViewById(R.id.search_button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				enableSearchPanel(false);
			}
		});

		searchEditText = (EditText) view.findViewById(R.id.search_edit_text);

		searchEditText.setOnTouchListener(this);
		searchEditText.addTextChangedListener(this);
		searchEditText.setOnEditorActionListener(this);
		searchEditText.setOnFocusChangeListener(this);

		searchEditText.setFocusable(false);

	}

	/**
	 * Activate/disable search panel
	 */
	public boolean onTouch(final View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			searchEditText.setFocusable(true);
			searchEditText.requestFocusFromTouch();
			if (!enabledSearchPanel) {
				enableSearchPanel(true);
			} else {
			}
		}
		return false;
	}

	public EditText getSearchEditText() {
		return searchEditText;
	}

	public void setSearchEditText(EditText searchEditText) {
		this.searchEditText = searchEditText;
	}

	public Button getButtonCancel() {
		return buttonCancel;
	}

	public void setButtonCancel(Button buttonCancel) {
		this.buttonCancel = buttonCancel;
	}

	/**
	 * 
	 */
	public void afterTextChanged(Editable s) {
		if (!applicationSetText) {
			String query = s.toString();
			int amount = query.length();
			if (amount == 0) {
				hideSearchForLabel(true);
			}

			if (searchListener != null) {
				searchListener.onAutoSuggestion(query);
			}

			if (!enabledSearchPanel) {
				enableSearchPanel(true);
			}
		} else {
			applicationSetText = false;
		}
	}

	/**
	 * Not use
	 */
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	/**
	 * Not use
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * Enter event occurs on the method call onClickSearchResult
	 */
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

			String query = v.getText().toString();
			searchEditText.setText(query);
			if (searchListener != null) {
				searchListener.onClickSearchResult(query);
			}
			hideKeyboard();
			return true;
		}
		return false;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus && v instanceof EditText) {
			hideKeyboard();
		}
	}

	/**
	 * Set visibility in true for search panel (cancel button and etc)
	 * 
	 * @param enable
	 *            if true then the search panel is appear otherwise disappear
	 */
	public void enableSearchPanel(boolean enable) {
		if (enable) {
			// Show Soft Keyboard
			showKeyboard();

			searchEditText.setEnabled(true);

			buttonCancel.setVisibility(View.VISIBLE);

			hideSearchForLabel(true);

			enabledSearchPanel = true;
			searchEditText.requestFocus();
		} else {
			clear();
			searchEditText.setFocusable(false);
			hideSearchForLabel(true);
			if (searchListener != null) {
				searchListener.onClear();
			}
		}
	}

	/**
	 * Invisible search for label
	 */
	public void hideSearchForLabel(boolean hidden) {
		if (searchEmptyText != null) {
			if (hidden) {
				searchEmptyText.setVisibility(View.GONE);
				searchEmptyText.setText("");
			} else {
				searchEmptyText.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Call before print search result
	 */
	public void beforeShowResult() {
	}

	/**
	 * Sets tag for finding frame1 (normal page) and frame2 (auto suggestion
	 * page)
	 * 
	 * @param tag
	 *            is unique string per page
	 * @throws SearchException
	 *             if tag not found
	 */
	public void setSearchTag(String tag) {
		searchEditText.setFocusable(false);
		if (searchEditText.getText().length() != 0) {
			searchEmptyText.setVisibility(View.VISIBLE);
		} else {
		}
		searchEditText.setOnFocusChangeListener(this);
		searchEditText.clearFocus();
	}

	/**
	 * Sets text query empty and clear search result
	 * 
	 */
	public void clear() {
		// Hide Soft Keyboard
		hideKeyboard();
		buttonCancel.setVisibility(View.GONE);

		searchEditText.setText("");

		enabledSearchPanel = false;
	}

	/**
	 * Sets text query empty and clear search result
	 * 
	 */
	public void clearWithoutQueryText() {
		// Hide Soft Keyboard
		hideKeyboard();
		buttonCancel.setVisibility(View.GONE);

		searchEditText.setText("");
		enabledSearchPanel = false;

		searchEmptyText.setVisibility(View.GONE);
		searchEmptyText.setText("");
	}

	public SearchListener getSearchListener() {
		return searchListener;
	}

	public void setSearchListener(SearchListener searchListener) {
		this.searchListener = searchListener;
	}

	public void setTextQuery(String text) {
		applicationSetText = true;
		searchEditText.setText(text);
	}

	public void setTextSearchResult(String text) {
		if (searchEmptyText == null)
			return;
		searchEmptyText.setText(text);
	}

	public void setSearchAdapter(ArrayAdapter<String> searchAdapter) {
	}

	public void hideKeyboard() {
		SystemHelper.hideKeyboard(context, searchEditText);

	}

	public void showKeyboard() {
		SystemHelper.showKeyboard(context, searchEditText);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		hideKeyboard();
	}
}
