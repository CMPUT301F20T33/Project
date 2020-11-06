package com.example.mobilibrary.DatabaseController;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobilibrary.Book;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class BookService {
    private static final String TAG = "AddBookFragment";
    //Singleton class implementation
    private static BookService bookDb = null;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    public static BookService getInstance(){
        if (BookService.bookDb == null)
            BookService.bookDb = new BookService();

        return BookService.bookDb;
    }

    private BookService(){
        db = FirebaseFirestore.getInstance();
    }

    public void addBook(final Context context, Book newBook){
        if (newBook.getFirestoreID()!= null)
            throw new IllegalArgumentException("This book is already added to the database");
        DocumentReference bookDoc = db.collection("Books").document(newBook.getTitle());
         Map<String, Object> data = new HashMap<>();
         data.put("ISBN", newBook.getISBN());
         data.put("Author", newBook.getAuthor());
         data.put("Status", newBook.getStatus());
         data.put("Owner", newBook.getOwner().getUsername());
         bookDoc.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 Toast.makeText(context, "Successfully added book!", Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.e(TAG,"Failed with: " +e.toString());
                 Toast.makeText(context, "Book not added!" , Toast.LENGTH_SHORT).show();
             }
         });
    }

    public void uploadImage(String title, Uri imageUri, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        StorageReference fileRef = storageReference.child(title);
        fileRef.putFile(imageUri)
                .continueWith(new Continuation<UploadTask.TaskSnapshot, Void>() {
                    @Override
                    public Void then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        task.getResult();
                        return null;
                    }
                })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

    }



}
