import { GraphQLClient } from "graphql-request";

const endpoint =
  import.meta.env.PUBLIC_API_URL || "http://localhost:8080/graphql";

export const client = new GraphQLClient(endpoint, {
  credentials: "include",
  mode: "cors",
});
