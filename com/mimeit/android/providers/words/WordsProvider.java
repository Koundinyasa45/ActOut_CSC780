package com.mimeit.android.providers.words;

import static com.mimeit.android.providers.words.WordsContract.TABLE_NAME;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.mimeit.android.providers.base.BaseProvider;

// content provider for words db
public class WordsProvider extends BaseProvider {

    @Override
    public boolean onCreate() {
        mUriMatcher.addURI(WordsContract.getAuthority(getContext()), null,
                URI_CONTENT);
        mUriMatcher.addURI(WordsContract.getAuthority(getContext()), "#",
                URI_CONTENT_ITEM);

        return super.onCreate();
    }// onCreate()

    @Override
    protected SQLiteOpenHelper newDbHelper() {
        return new WordsHelper(getContext());
    }// newDbHelper()

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }// getTableName()

    @Override
    protected Uri getContentItemBaseUri() {
        return WordsContract.getContentItemBaseUri(getContext());
    }// getContentItemBaseUri()

    @Override
    protected String getDefaultSortOrder() {
        return null;
    }// getDefaultSortOrder()

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
        case URI_CONTENT:
            return WordsContract.getContentType(getContext());
        case URI_CONTENT_ITEM:
            return WordsContract.getContentItemType(getContext());
        }

        return null;
    }// getType()

}
