import {type GroceryItem} from "@/model/GroceryItem.ts";

export type GroceryList = {
  id: number
  name: string
}

// EK
export type GroceryListWithItems = {
  items: Array<GroceryItem>
} & GroceryList
