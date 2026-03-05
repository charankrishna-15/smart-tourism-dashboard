$webRoot = Join-Path $PSScriptRoot "web"
$port = 8080
$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://localhost:$port/")

try {
    $listener.Start()
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host " Smart Tourism Web Dashboard Running!" -ForegroundColor Green
    Write-Host " URL: http://localhost:$port" -ForegroundColor Green
    Write-Host " Press Ctrl+C in this terminal to stop the server." -ForegroundColor Yellow
    Write-Host "==========================================" -ForegroundColor Cyan

    Start-Process "http://localhost:$port"

    while ($listener.IsListening) {
        $ctx = $listener.GetContext()
        $req = $ctx.Request
        $res = $ctx.Response
        
        $path = $req.Url.LocalPath.TrimStart('/')
        if ([string]::IsNullOrEmpty($path)) { $path = 'index.html' }
        
        $file = Join-Path $webRoot $path
        
        if (Test-Path $file -PathType Leaf) {
            $ext = [IO.Path]::GetExtension($file).ToLower()
            $mime = switch ($ext) {
                '.html' { 'text/html' }
                '.css'  { 'text/css' }
                '.js'   { 'application/javascript' }
                '.png'  { 'image/png' }
                '.jpg'  { 'image/jpeg' }
                '.webp' { 'image/webp' }
                default { 'application/octet-stream' }
            }
            $res.ContentType = $mime
            
            $bytes = [IO.File]::ReadAllBytes($file)
            $res.ContentLength64 = $bytes.Length
            $res.OutputStream.Write($bytes, 0, $bytes.Length)
        } else {
            $res.StatusCode = 404
        }
        $res.OutputStream.Close()
    }
} catch {
    Write-Host "Error starting server: $_" -ForegroundColor Red
    Write-Host "Port $port might be in use by another program." -ForegroundColor Yellow
} finally {
    if ($listener.IsListening) { $listener.Stop() }
}
