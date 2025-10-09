function App() {
  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="container mx-auto p-8">
        <h1 className="text-4xl font-bold mb-8">Turn App Frontend</h1>
        <p className="text-muted-foreground">
          Proyecto base configurado con Vite, React, TypeScript y Tailwind CSS.
          Todas las dependencias están instaladas y listas para usar.
        </p>
        <div className="mt-8 p-6 border rounded-lg">
          <h2 className="text-2xl font-semibold mb-4">Dependencias incluidas:</h2>
          <ul className="list-disc list-inside space-y-2 text-sm">
            <li>React Hook Form + Zod para formularios y validación</li>
            <li>Radix UI para componentes de interfaz</li>
            <li>Tailwind CSS para estilos</li>
            <li>Lucide React para iconos</li>
            <li>Recharts para gráficos</li>
            <li>Sonner para notificaciones</li>
            <li>Date-fns para manejo de fechas</li>
            <li>Y muchas más utilidades...</li>
          </ul>
        </div>
      </div>
    </div>
  )
}

export default App
