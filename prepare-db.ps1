Write-Host "Остановка контейнеров..."
docker-compose down -v

Write-Host "Запуск MySQL..."
docker-compose up -d mysql

Write-Host "Ожидание инициализации MySQL..."
Start-Sleep -Seconds 15

Write-Host "Очистка данных..."
$connectionString = "Server=localhost;Database=app;Uid=app;Pwd=pass;"
$connection = New-Object MySql.Data.MySqlClient.MySqlConnection
$connection.ConnectionString = $connectionString
$connection.Open()

$commands = @(
    "DELETE FROM auth_codes;",
    "DELETE FROM card_transactions;",
    "DELETE FROM cards;",
    "DELETE FROM users;"
)

foreach ($cmd in $commands) {
    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($cmd, $connection)
    $command.ExecuteNonQuery()
}

$connection.Close()
Write-Host "База готова!"