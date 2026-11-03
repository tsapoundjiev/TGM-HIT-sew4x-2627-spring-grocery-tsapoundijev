export type Nullable<T, K extends keyof T> = { [P in K]: T[P] | null } & Omit<T, K>
export type New<T extends { id: number }> = Nullable<T, 'id'>
