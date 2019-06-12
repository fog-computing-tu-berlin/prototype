-- Change default privileges for rpc function
ALTER DEFAULT PRIVILEGES REVOKE EXECUTE ON FUNCTIONS FROM PUBLIC;

-- the names "anon" and "authenticator" are configurable and not
-- sacred, we simply choose them for clarity
CREATE ROLE anon;
CREATE ROLE authenticator noinherit;
GRANT anon TO authenticator;

GRANT usage ON schema public TO anon;

-- pgcrypto is required for hash functions
-- CREATE extension pgcrypto;
-- GRANT EXECUTE ON FUNCTION public.digest(text, text) TO anon;
-- GRANT EXECUTE ON FUNCTION public.digest(bytea, text) TO anon;

-- grant usage on schema public, basic_auth to anon;
-- grant select on table pg_authid, basic_auth.users to anon;
-- grant execute on function login(text,text) to anon;