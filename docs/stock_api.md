# Stock API

This backend exposes the stock module through GraphQL at:

`http://localhost:8080/graphql`

Use `Content-Type: application/json` for requests.

## Item type

- `id: ID!`
- `name: String!`
- `description: String`

## Queries

### `items`
Returns all stock items.

```json
{
  "query": "query { items { id name description } }"
}
```

### `item(id: ID!)`
Returns one item by id.

```json
{
  "query": "query GetItem($id: ID!) { item(id: $id) { id name description } }",
  "variables": {
    "id": 1
  }
}
```

## Mutations

### `createItem(name: String!, description: String)`
Creates a new item.

```json
{
  "query": "mutation CreateItem($name: String!, $description: String) { createItem(name: $name, description: $description) { id name description } }",
  "variables": {
    "name": "Sample item",
    "description": "Created from the stock API"
  }
}
```

### `updateItem(id: ID!, name: String!, description: String)`
Updates an existing item.

```json
{
  "query": "mutation UpdateItem($id: ID!, $name: String!, $description: String) { updateItem(id: $id, name: $name, description: $description) { id name description } }",
  "variables": {
    "id": 1,
    "name": "Updated item",
    "description": "Updated from the stock API"
  }
}
```

### `deleteItem(id: ID!)`
Deletes an item and returns `true` when the delete succeeds.

```json
{
  "query": "mutation DeleteItem($id: ID!) { deleteItem(id: $id) }",
  "variables": {
    "id": 1
  }
}
```

## Quick test order

1. Run `items` to confirm the backend is reachable.
2. Create an item with `createItem`.
3. Fetch it with `item(id)`.
4. Update it with `updateItem`.
5. Remove it with `deleteItem`.
