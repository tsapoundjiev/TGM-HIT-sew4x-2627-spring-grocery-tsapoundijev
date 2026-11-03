<script setup lang="ts">
import {computed} from "vue"
import {type GroceryItem} from "@/model/GroceryItem.ts"
import {type GroceryList} from "@/model/GroceryList.ts"
import GroceryListSelectorComponent from "@/components/GroceryListSelectorComponent.vue";
import {CheckIcon, PencilIcon, TrashIcon, XMarkIcon} from "@heroicons/vue/24/outline"

const props = defineProps<{
  item: GroceryItem,
  lists: GroceryList[]
}>()

const checkboxId = computed(() => 'checkbox-item-' + props.item.id)

const listName = computed(() => props.lists.find((list) => list.id == props.item.listId)?.name ?? '')

const editMode = false // TODO Make reactive

let counter = 0
</script>
<template>
  <div class="flex gap-2 justify-between items-center border-b border-gray-200 dark:border-gray-700 p-2">
    <input type="checkbox" @click="console.error('patchCollected not implemented')" class="w-4 h-4 m-2.5" :id="checkboxId"  :disabled="editMode"/>

    <div class="flex flex-1 flex-wrap gap-2" :class="{'line-through text-gray-400 dark:text-gray-500' : item.collected}">
      <div class="w-full sm:w-12">
        <input v-if="editMode" type="number" placeholder="Anzahl" v-on:keyup.enter="console.error('save not implemented')" v-on:keyup.esc="console.error('cancel not implemented')" class="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100 rounded px-2 h-9 w-full">
        <label v-else class="flex items-center h-9 border border-transparent px-2" :for="checkboxId">{{ item.amount }}</label>
      </div>

      <div class="w-full sm:flex-1">
        <input v-if="editMode" type="text" placeholder="Name" v-on:keyup.enter="console.error('save not implemented')" v-on:keyup.esc="console.error('cancel not implemented')" class="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100 rounded px-2 h-9 w-full">
        <label v-else class="flex items-center border border-transparent px-2" :for="checkboxId">{{ item.name }}</label>
      </div>

      <div class="w-full sm:w-auto">
        <GroceryListSelectorComponent v-if="editMode" :lists="lists"/><!-- Wiederhole mich -->
        <label v-else class="flex items-center h-9 border border-transparent px-2" :for="checkboxId">{{ listName }}</label>
      </div>
    </div>

    <div class="flex flex-col sm:flex-row items-center justify-center gap-2">
      <button v-if="editMode" type="button" aria-label="Speichern" class="border border-blue-600 dark:border-blue-400 text-blue-600 dark:text-blue-400 rounded h-9 w-9 flex items-center justify-center hover:bg-blue-50 dark:hover:bg-blue-950" @click="console.error('save not implemented')"><CheckIcon class="w-5 h-5"/></button>
      <button v-else type="button" aria-label="Bearbeiten" class="border border-blue-600 dark:border-blue-400 text-blue-600 dark:text-blue-400 rounded h-9 w-9 flex items-center justify-center hover:bg-blue-50 dark:hover:bg-blue-950" @click="console.error('edit not implemented')"><PencilIcon class="w-5 h-5"/></button>
      <button v-if="editMode" type="button" aria-label="Abbrechen" class="border border-gray-400 dark:border-gray-500 text-gray-600 dark:text-gray-400 rounded h-9 w-9 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-800" @click="console.error('cancel not implemented')"><XMarkIcon class="w-5 h-5"/></button>
      <button v-else type="button" aria-label="Löschen" class="border border-red-600 dark:border-red-400 text-red-600 dark:text-red-400 rounded h-9 w-9 flex items-center justify-center hover:bg-red-50 dark:hover:bg-red-950" @click="console.error('delete not implemented')"><TrashIcon class="w-5 h-5"/></button>
      <span title="Renderzyklen" class="inline-flex items-center justify-center w-9 h-9 rounded-full border border-blue-600 dark:border-blue-400 text-blue-600 dark:text-blue-400 text-m">{{ ++counter }}</span>
    </div>
  </div>
</template>
