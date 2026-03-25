<template>
  <div class="editor-container">
    <div class="editor-toolbar">
      <span>Hỗ trợ Markdown cơ bản</span>
    </div>
    <textarea ref="editorRef"></textarea>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/mode/markdown/markdown'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
let cmInstance = null
let isUpdating = false

onMounted(() => {
  cmInstance = CodeMirror.fromTextArea(editorRef.value, {
    mode: 'markdown',
    theme: 'dracula',
    lineNumbers: true,
    lineWrapping: true,
    viewportMargin: Infinity
  })

  cmInstance.setValue(props.modelValue)

  cmInstance.on('change', (cm) => {
    isUpdating = true
    emit('update:modelValue', cm.getValue())
    isUpdating = false
  })
})

watch(() => props.modelValue, (newVal) => {
  if (!isUpdating && cmInstance && newVal !== cmInstance.getValue()) {
    const cursor = cmInstance.getCursor()
    cmInstance.setValue(newVal)
    cmInstance.setCursor(cursor)
  }
})

onBeforeUnmount(() => {
  if (cmInstance) {
    cmInstance.toTextArea()
  }
})
</script>

<style>
.editor-container {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
}

.editor-toolbar {
  background: #f3f4f6;
  padding: 8px 12px;
  border-bottom: 1px solid var(--border-color);
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.CodeMirror {
  height: auto;
  min-height: 250px;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 14px;
}
</style>
