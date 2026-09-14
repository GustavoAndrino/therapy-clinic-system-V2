# Architecture and Design Decisions

This document records the main architectural decisions made during the
development of the Practice Management System, including the problems that
motivated them and the reasoning behind the chosen solutions.

## 1. Workspace Membership Model

### Context

The original model established a direct relationship between users and
workspaces. However, a user may eventually belong to multiple workspaces
and have a different role in each one.

### Decision

The direct User–Workspace relationship was replaced with a
`WorkspaceMembership` entity.

A membership connects:

- a `User`
- a `Workspace`
- a `WorkspaceRole`

This allows, for example, the same user to be an `OWNER` of one workspace
while being a `THERAPIST` in another.

### Why

A role does not describe the user globally. It describes the user's
relationship with a particular workspace.

This model also allows the application to support both individual
practitioners and multi-user clinics without requiring separate account
architectures.

## 2. Workspace-Based Billing

### Context

Initially, billing could have been associated directly with the user who
created an account.

However, users are not necessarily responsible for payment. A therapist
may simply be a member of a larger clinic.

### Decision

Subscriptions and billing belong to the `Workspace`, rather than to
individual `User` accounts.

The user who creates a workspace initially receives the `OWNER` role and
can manage its billing settings.

### Why

The workspace represents the subscribed organization or practice.

This allows multiple users to participate in a clinic without requiring
each user to have billing information and also allows one user to
participate in multiple independently billed workspaces.

## 3. API Request Strategy

### Context

Loading application state through many small API requests introduces
additional network round trips and makes frontend initialization more
complex.

### Decision

Where appropriate, endpoints used to initialize a user's workspace return
the related information required by the client together rather than
requiring an independent request for every piece of data.

### Why

This reduces unnecessary client-server round trips while keeping
specialized operations available through dedicated endpoints.