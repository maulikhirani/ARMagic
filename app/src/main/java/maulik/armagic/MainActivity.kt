package maulik.armagic

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.ar.core.Config
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import maulik.armagic.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private var selectedArModel: ArModel? = null
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val adapter: ModelSelectionAdapter = ModelSelectionAdapter {
        selectModelAndRefreshList(it)
    }

    private fun selectModelAndRefreshList(arModel: ArModel) {
        selectedArModel = arModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIfOpenGl3SupportedOrFinish()) return
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        arFragment.setOnSessionInitializationListener {
            it.configure(
                it.config.setPlaneFindingMode(Config.PlaneFindingMode.VERTICAL)
            )
        }

        initModelList()

        mainViewModel.createModels()
        mainViewModel.arModelList.observe(this) {
            adapter.submitList(it)
        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (selectedArModel == null) return@setOnTapArPlaneListener

            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

         /*   val intermediateNode = Node()
            intermediateNode.setParent(anchorNode)
            val anchorUp = anchorNode.up
            intermediateNode.setLookDirection(Vector3.up(), anchorUp)*/


            // Create the transformable model and add it to the anchor.
            val model = TransformableNode(arFragment.transformationSystem)
            if (selectedArModel?.scaleOptions?.freeScaling == false) {
                model.scaleController.minScale = selectedArModel?.scaleOptions?.minScale ?: 0.1f
                model.scaleController.maxScale = selectedArModel?.scaleOptions?.maxScale ?: 1.0f
            }
            /*model.localRotation =
                Quaternion.axisAngle(Vector3(-1f, 0f, 0f), 90f)*/
            model.setParent(anchorNode)
            model.renderable = selectedArModel?.renderable
            model.select()
        }

    }

    private fun initModelList() {
        binding.rvModels.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvModels.adapter = adapter

        val selectionPredicate = ArModelSelectionPredicate()

        val tracker = SelectionTracker.Builder(
            "modelSelection",
            binding.rvModels,
            ArModelKeyProvider(adapter),
            ArModelKeyLookup(binding.rvModels),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            selectionPredicate
        ).build()

        selectionPredicate.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onItemStateChanged(key: String, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                if (selected) {
                    selectedArModel = adapter.currentList.find {
                        it.resourceId.toString() == key
                    }
                }
                Log.d("SelectionSize", tracker.selection.size().toString())
            }
        })

        adapter.tracker = tracker
    }

}