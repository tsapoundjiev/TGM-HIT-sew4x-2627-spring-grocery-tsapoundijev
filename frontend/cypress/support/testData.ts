export interface TestItem {
  id: number
  name: string
  amount: number
  collected: boolean
  listId: number | null
}

export interface TestList {
  id: number
  name: string
  items: TestItem[]
}

// Mirrors the shape returned by GET /api/grocery/list (GroceryListWithItems[]).
export const testLists: TestList[] = [
  {
    id: 1,
    name: 'Supermarkt',
    items: [
      { id: 1, name: 'Milch', amount: 2, collected: false, listId: 1 },
      { id: 2, name: 'Butter', amount: 1, collected: true, listId: 1 },
      { id: 3, name: 'Apfel', amount: 6, collected: false, listId: 1 },
    ],
  },
  {
    id: 2,
    name: 'Baumarkt',
    items: [{ id: 4, name: 'Schrauben', amount: 50, collected: false, listId: 2 }],
  },
]

export const testItems: TestItem[] = testLists.flatMap((list) => list.items)

export function findItem(id: number): TestItem {
  const item = testItems.find((i) => i.id === id)
  if (!item) {
    throw new Error(`Unknown test item id ${id}`)
  }
  return item
}

export function interceptLoadList() {
  return cy.intercept('GET', '/api/grocery/list', { statusCode: 200, body: testLists }).as('loadList')
}

export function visitAndLoad() {
  interceptLoadList()
  cy.visit('/')
  return cy.wait('@loadList')
}
