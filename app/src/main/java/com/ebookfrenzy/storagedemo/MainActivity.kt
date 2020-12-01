package com.ebookfrenzy.storagedemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private val CREATE_REQUEST_CODE = 40
    private val OPEN_REQUEST_CODE = 41
    private val SAVE_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newFile()
        openFile()
        saveFile()
    }

    private fun newFile() {

        newButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "newfile.text")

            startActivityForResult(intent,CREATE_REQUEST_CODE)
        }
    }

    private fun openFile() {

        openButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"

            startActivityForResult(intent,OPEN_REQUEST_CODE)
        }
    }

    private fun saveFile() {

        saveButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"

            startActivityForResult(intent, SAVE_REQUEST_CODE)
        }

    }

    private fun writeFileContent(uri: Uri) {

        try {

            val pfd = contentResolver.openFileDescriptor(uri, "w")
            val fileOutputStream = FileOutputStream(pfd?.fileDescriptor)
            val textConent = fileText.text.toString()

            fileOutputStream.write(textConent.toByteArray())
            fileOutputStream.close()

            pfd?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var currentUri: Uri? = null

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Activity.RESULT_OK) {

                if (data != null) {
                    fileText.setText("")
                }
            }
        } else if (requestCode == SAVE_REQUEST_CODE) {

            data?.let {

                currentUri = it.data
                currentUri?.let {
                    writeFileContent(it)
                }
            }
        } else if (requestCode == OPEN_REQUEST_CODE) {

            data?.let {
                currentUri = it.data

                currentUri?.let {

                    try {

                        val content = readFileConent(it)
                        fileText.setText(content)
                    } catch (e: IOException) {
                        //Handle error here
                    }
                }
            }
        }
    }

    private fun readFileConent(uri: Uri): String {


        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        val currentline = reader.readLine()

        while (currentline != null) {
            stringBuilder.append(currentline + "\n")
        }
        inputStream?.close()
        return stringBuilder.toString()
    }
}