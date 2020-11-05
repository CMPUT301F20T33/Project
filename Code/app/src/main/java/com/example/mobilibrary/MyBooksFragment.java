package com.example.mobilibrary;

import android.content.Intent;
import android.os.Bundle;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.mobilibrary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * My Books fragment that is navigated to using notification bar. Contains a dropdown that organizes the User's books into status:
 * Owned, Requested, Accepted, and Borrowed. The user is able to see book title, author, isbn, and status.
 * The user is also able to add and edit their books in this Fragment
 *
 */
public class MyBooksFragment extends Fragment {
    private ListView bookView;
    private ArrayAdapter<Book> bookAdapter;
    private ArrayList<Book> bookList;
    private FloatingActionButton addButton;

    private ArrayList<Book> tempBookList;
    private Spinner statesSpin;
    private static final String[] states = new String[]{"Owned", "Requested", "Accepted", "Borrowed"};

    public MyBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("In MyBooks Fragment");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_books, container, false);
        addButton = (FloatingActionButton) v.findViewById(R.id.addButton);
        bookView = (ListView) v.findViewById(R.id.book_list);
        bookList = new ArrayList<Book>();

        tempBookList = new ArrayList<Book>();

        bookAdapter = new customBookAdapter(this.getActivity(), bookList);
        bookView.setAdapter(bookAdapter);

        statesSpin = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<String> SpinAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, states);
        SpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statesSpin.setAdapter(SpinAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getActivity(), AddBookFragment.class);
                startActivityForResult(addIntent, 0);
            }
        });

        bookView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = bookList.get(i);
                Intent viewBook = new Intent(getActivity(), BookDetailsFragment.class);
                viewBook.putExtra("view book", book);
                // viewBook.putExtra("book owner", user.getusername());   // need to get user somehow, add User variable to this class
                startActivityForResult(viewBook, 1);
            }
        });

        statesSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String state = (String) adapterView.getItemAtPosition(i);
                DisplayBooks(state);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }

    /**
     * If requestCode is 0, if its 1, we are either deleting a book (result code =1) or editing
     * an existing book (result code = 2) with data.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Book new_book = (Book) Objects.requireNonNull(data.getExtras()).getSerializable("new book");
                bookAdapter.add(new_book);
                tempBookList.add(new_book);
                bookAdapter.notifyDataSetChanged();
            }
        }


        if (requestCode == 1) {
            if (resultCode == 1) {
                // book needs to be deleted, intent has book to delete
                Book delete_book = (Book) data.getSerializableExtra("delete book");

                // find the book to delete and delete it
                for (int i = 0; 0 < bookAdapter.getCount(); i++) {
                    Book currentBook = bookAdapter.getItem(i);
                    if (delete_book.compareTo(currentBook) == 0) {
                        tempBookList.remove(currentBook);
                        bookAdapter.remove(currentBook);
                    }
                }

                bookAdapter.notifyDataSetChanged();
            } else if (resultCode == 2) {
                // book was edited update data set
                Book edited_book = (Book) data.getSerializableExtra("edited book");

                // find the book to edit and edit it
                for (int i = 0; i < bookList.size(); i++) {
                    Book currentBook = bookList.get(i);
                    if (edited_book.compareTo(currentBook) == 0) {
                        currentBook.setTitle(edited_book.getTitle());
                        currentBook.setAuthor(edited_book.getAuthor());
                        currentBook.setISBN(edited_book.getISBN());
                        currentBook.setImage(edited_book.getImage());
                    }
                }
                bookAdapter.notifyDataSetChanged();
            }
        }
    }

    // userBookList

    /**
     * Used to function spinner, if the book is in a certain status it will group them and will
     * allow the user to view them as such
     *
     * @param state
     */
    public void DisplayBooks(String state) {
        state = state.toLowerCase();
        switch (state) {
            case "requested":
                for (Book book : tempBookList) {
                    if (book.getStatus() != state) {
                        if (bookList.contains(book) == true) {
                            bookAdapter.remove(book);
                        }
                    }
                }
                bookAdapter.notifyDataSetChanged();
                break;
            case "owned":
                Log.d("sooraj", "owned is pressed");
                for (Book book : tempBookList) {
                    Log.d("sooraj", "booklist doesnt contain a book, add it");
                    bookAdapter.add(book);
                }
                bookAdapter.notifyDataSetChanged();
                break;
            case "accepted":
                for (Book book : tempBookList) {
                    if (book.getStatus() != state) {
                        if (bookList.contains(book) == true) {
                            bookAdapter.remove(book);
                        }
                    }
                }
                bookAdapter.notifyDataSetChanged();
                break;

            case "borrowed":
                for (Book book : tempBookList) {
                    if (book.getStatus() != state) {
                        if (bookList.contains(book) == true) {
                            bookAdapter.remove(book);
                        }
                    }
                }
                bookAdapter.notifyDataSetChanged();
                break;
        }
    }
}