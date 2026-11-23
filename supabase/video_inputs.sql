-- Run this once in the Supabase SQL editor.
-- Anonymous sign-ins must also be enabled in Authentication > Providers > Anonymous.

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values (
    'video-inputs',
    'video-inputs',
    true,
    1048576,
    array['image/jpeg']
)
on conflict (id) do update set
    public = excluded.public,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;

drop policy if exists "authenticated users upload video inputs" on storage.objects;

create policy "authenticated users upload video inputs"
on storage.objects
for insert
to authenticated
with check (
    bucket_id = 'video-inputs'
    and (storage.foldername(name))[1] = (select auth.uid()::text)
);
