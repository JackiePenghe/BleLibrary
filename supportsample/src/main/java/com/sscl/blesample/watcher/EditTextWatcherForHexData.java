package com.sscl.blesample.watcher;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.sscl.baselibrary.utils.DebugUtil;


/**
 * @author jacke
 * @date 2018/1/22 0022
 */

public class EditTextWatcherForHexData implements TextWatcher {

    private static final String TAG = EditTextWatcherForHexData.class.getSimpleName();
    private static final char CHAR_0 = '0';
    private static final char CHAR_9 = '9';
    private static final char CHAR_A = 'A';
    private static final char CHAR_F = 'F';
    private static final char CHAR_LOW_A = 'a';
    private static final char CHAR_LOW_F = 'f';

    private boolean mFormat;

    private boolean mInvalid;

    private int mSelection;

    private String mLastText = "";

    private EditText editText;


    public EditTextWatcherForHexData(EditText editText) {
        this.editText = editText;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s CharSequence
     * @param start int
     * @param count int
     * @param after after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param charSequence CharSequence
     * @param start int
     * @param before int
     * @param count int
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        try {

            String temp = charSequence.toString();

            // Set selection.
            if (mLastText.equals(temp)) {
                if (mInvalid) {
                    mSelection -= 1;
                } else {
                    if (mSelection >= 1 && temp.length() > mSelection - 1
                            && temp.charAt(mSelection - 1) == ' ') {
                        mSelection += 1;
                    }
                }
                int length = mLastText.length();
                if (mSelection > length) {
                    editText.setSelection(length);
                } else {
                    editText.setSelection(mSelection);
                }
                mFormat = false;
                mInvalid = false;
                return;
            }

            mFormat = true;
            mSelection = start;

            // Delete operation.
            if (count == 0) {
                if (mSelection >= 1 && temp.length() > mSelection - 1
                        && temp.charAt(mSelection - 1) == ' ') {
                    mSelection -= 1;
                }

                return;
            }

            // Input operation.
            mSelection += count;
            char[] lastChar = temp.substring(start, start + count)
                    .toCharArray();
            int mid = lastChar[0];

            //        字符   0-9.
            //noinspection StatementWithEmptyBody
            if (CHAR_0 <= mid && mid <= CHAR_9) {
            }
            //                 A-F.
            else //noinspection StatementWithEmptyBody
                if (CHAR_A <= mid && mid <= CHAR_F) {
            }
            //                 把 a-f转为A-F.
            else if (CHAR_LOW_A <= mid && mid <= CHAR_LOW_F) {

                for (int i = 0; i < lastChar.length; i++) {
                    lastChar[i] = (char) (lastChar[i] - 32);
                }


                temp = temp.substring(0, start) + new String(lastChar);
                editText.setText(temp);
            }
            /* Invalid input. */
            else {
                mInvalid = true;
                temp = temp.substring(0, start) + temp.substring(start + count);
                editText.setText(temp);
            }

        } catch (Exception e) {
            DebugUtil.warnOut(TAG, e.getMessage());
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param editable Editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        try {

            /* Format input. */
            if (mFormat) {
                StringBuilder text = new StringBuilder();
                text.append(editable.toString().replace(" ", ""));
                int length = text.length();
                int sum;
                if (length % 2 == 0) {
                    sum = length / 2 - 1;
                } else {
                    sum = length / 2;
                }
                int offset = 2;
                int index = 0;
                while (index < sum) {

                    text.insert(offset, " ");
                    offset += 3;
                    index++;
                }
                mLastText = text.toString();
                editText.setText(text);
            }
        } catch (Exception e) {
            DebugUtil.warnOut(TAG, e.getMessage());
        }
    }
}
