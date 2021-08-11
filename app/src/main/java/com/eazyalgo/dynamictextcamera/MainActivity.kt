package com.eazyalgo.dynamictextcamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.eazyalgo.dynamictextcamera.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    val map = HashMap<Int,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider = cameraProvider)

        },ContextCompat.getMainExecutor(this))

        map.put(1,"Apple")
        map.put(2,"Orange")
        map.put(3,"Banana")
        map.put(4,"Milk")
        map.put(5,"Drinks")
        map.put(6,"Bread")
        map.put(7,"Peanut")
        map.put(8,"Cake")
        map.put(9,"Carrot")
        map.put(10,"Tomato")

        getData(map)

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        val preview = Preview.Builder().build()
        val viewFinder: PreviewView = findViewById(R.id.previewCamera)

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider())
    }

    private fun getData(map: HashMap<Int,String>){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(MyApi::class.java)

        val retrofitData = retrofitBuilder.getDataById(randomId())
        retrofitData.enqueue(object : Callback<DataItem> {
            override fun onResponse(call: Call<DataItem>?, response: Response<DataItem>?) {

                val responseBody = response?.body()
                // parse the json response here
                // set the text over camera here according to data from api call

                val id = responseBody?.id.toString()
                val name = map.get(responseBody?.id)
                val des = responseBody?.title

                Log.i("here1" , id + " " + name + " " + des)

                binding.productId.visibility = View.VISIBLE
                binding.productName.visibility = View.VISIBLE
                binding.productDes.visibility = View.VISIBLE

                binding.textRelativeLayout.setBackgroundResource(R.drawable.layout_background)

                binding.productId.text = "Id :" + id
                binding.productName.text = "Name :" +name
                binding.productDes.text = des

            }

            override fun onFailure(call: Call<DataItem>?, t: Throwable?) {
                Log.i("Error" , "Error occurred")
            }
        })
    }

    // return random number between 1 to 10
    private fun randomId(): Int {
        val rand = Random
        val max = 10;
        val min = 1

        return rand.nextInt(max - min + 1) + min
    }
}