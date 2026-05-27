import { gql } from "graphql-request";

export const GET_ITEMS = gql`
  query {
    items {
      id
      name
      description
    }
  }
`;

export const GET_ITEM = gql`
  query ($id: ID!) {
    item(id: $id) {
      id
      name
      description
    }
  }
`;

export const CREATE_ITEM = gql`
  mutation ($name: String!, $description: String!) {
    createItem(name: $name, description: $description) {
      id
      name
      description
    }
  }
`;

export const UPDATE_ITEM = gql`
  mutation ($id: ID!, $name: String!, $description: String!) {
    updateItem(id: $id, name: $name, description: $description) {
      id
      name
      description
    }
  }
`;

export const DELETE_ITEM = gql`
  mutation ($id: ID!) {
    deleteItem(id: $id)
  }
`;
